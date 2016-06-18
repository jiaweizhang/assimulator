package assemble.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by jiaweizhang on 6/17/2016.
 */
public class AssemblyParser {
    // map from instruction name to grammar describing the instruction
    private Map<String, InstructionGrammar> instructionGrammarMap;
    private Map<String, PseudoInstructionGrammar> pseudoInstructionGrammarMap;

    // list of errors generated
    private List<String> errors;

    private List<String> binaryOutput;

    public AssemblyParser() {
        this.instructionGrammarMap = new HashMap<>();
        this.pseudoInstructionGrammarMap = new HashMap<>();
    }

    /***
     * Resets errors
     */
    private void resetErrors() {
        this.errors = new ArrayList<String>();
    }

    /***
     * Retrieves errors
     * @return list of errors generated
     */
    public List<String> getErrors() {
        return this.errors;
    }

    /***
     * Set the ISA
     * @param instructionGrammars specified ISA
     */
    public void setInstructionGrammars(List<InstructionGrammar> instructionGrammars) {
        this.instructionGrammarMap = instructionGrammars
                .stream()
                .collect(Collectors.toMap(ig -> ig.name, ig -> ig));
    }

    /***
     * Set the pseudoinstructions
     */
    public void setPseudoInstructionGrammars(List<PseudoInstructionGrammar> pseudoInstructionGrammars) {
        this.pseudoInstructionGrammarMap = pseudoInstructionGrammars
                .stream()
                .collect(Collectors.toMap(ig -> ig.name, ig -> ig));
    }

    /***
     * Process using the current ISA
     * @param assembly
     * @return true if no errors
     */
    public boolean process(List<String> assembly) {
        // clear errors
        this.resetErrors();

        // clear output
        this.binaryOutput = new ArrayList<String>();

        // assign line numbers
        // lambdadize when I feel like it
        List<Line> lineNumbers = new ArrayList<>();
        for (int i=0; i<assembly.size(); i++) {
            Line line = new Line();
            line.line = i+1;
            line.text = assembly.get(i);
            lineNumbers.add(line);
        }

        // remove whitespace and comments
        List<Line> noComments = lineNumbers
                .stream()
                .map(l -> {
                    String text = l.text;
                    // remove everything after comment
                    if (text.contains(";")) {
                        text = text.substring(0, text.indexOf(";"));
                    }
                    // trim whitespace
                    Line line = new Line();
                    line.line = l.line;
                    line.text = text.trim();
                    return line;
                })
                .filter(l -> l.text.length() > 0)
                .collect(Collectors.toList());

        // split text and data sections and remove headers
        Line textHeaderLine = noComments
                .stream()
                .filter(l -> l.text.indexOf(".text") == 0)
                .findFirst()
                .get();

        Line dataHeaderLine = noComments
                .stream()
                .filter(l -> l.text.indexOf(".data") == 0)
                .findFirst()
                .get();

        List<Line> textLines = noComments
                .stream()
                .filter(l -> textHeaderLine.line > dataHeaderLine.line
                    ? l.line >= textHeaderLine.line
                    : l.line >= textHeaderLine.line && l.line < dataHeaderLine.line)
                .map(l -> {
                    Line line = new Line();
                    line.line = l.line;
                    if (l.text.indexOf(".text") == 0) {
                        line.text = l.text.substring(5).trim();
                    } else {
                        line.text = l.text;
                    }
                    return line;
                })
                .filter(l -> l.text.length() > 0)
                .collect(Collectors.toList());

        List<Line> dataLines = noComments
                .stream()
                .filter(l -> dataHeaderLine.line > textHeaderLine.line
                        ? l.line >= dataHeaderLine.line
                        : l.line >= dataHeaderLine.line && l.line < textHeaderLine.line)
                .map(l -> {
                    Line line = new Line();
                    line.line = l.line;
                    if (l.text.indexOf(".data") == 0) {
                        line.text = l.text.substring(5).trim();
                    } else {
                        line.text = l.text;
                    }
                    return line;
                })
                .filter(l -> l.text.length() > 0)
                .collect(Collectors.toList());

        // generate data map
        int currentMemoryIndex = 0;

        // map from variable to memory location
        Map<String, Integer> dataVarMap = new HashMap<String, Integer>();
        List<Integer> dataMemory = new ArrayList<>();

        // lambda-ize when I feel like it
        for (Line l : dataLines) {
            // parse into 3 sections
            String[] arr = l.text.split("\\s+");
            if (arr.length != 3) {
                errors.add(l.line + "::not valid data format");
            }


            Matcher matcher = Pattern.compile("([a-zA-Z0-9]+):").matcher(arr[0]);
            if (matcher.find()) {
                String varName = matcher.group(1);
                switch (arr[1]) {
                    case ".word":
                        // always size 32 bits -> 1 memory address
                        dataVarMap.put(varName, currentMemoryIndex);
                        currentMemoryIndex++;
                        dataMemory.add(Integer.decode(arr[2]));
                        break;
                    case ".string":
                        // variable size, 32 bits per char because this ISA is not the best
                        dataVarMap.put(varName, currentMemoryIndex);
                        currentMemoryIndex += arr[2].length();
                        // store ascii values into data memory
                        for (char c : arr[2].toCharArray()) {
                            dataMemory.add((int)c);
                        }
                        break;
                    case ".char":
                        // size is 32 bits because we can
                        dataVarMap.put(varName, currentMemoryIndex);
                        currentMemoryIndex++;
                        dataMemory.add((int)arr[2].charAt(0));
                        break;
                    case ".int":
                        // subset of .word
                        dataVarMap.put(varName, currentMemoryIndex);
                        currentMemoryIndex++;
                        dataMemory.add(Integer.parseInt(arr[2]));
                        break;
                    default:
                        errors.add(l.line + "::unrecognized data type");
                        break;
                }
            } else {
                errors.add(l.line+"::invalid var name");
                return false;
            }
        }

        // process labels and generate temporary label map
        Map<String, Integer> tempLabelMap = new HashMap<String, Integer>();
        List<Line> noLabels = new ArrayList<Line>();
        Pattern pattern = Pattern.compile("([a-zA-Z0-9]+):.*");
        for (Line l : textLines) {
            // compile the regex group capture

            // attempt to match the groups
            Matcher matcher = pattern.matcher(l.text);

            Line line = new Line();
            line.line = l.line;
            if (matcher.find()) {
                // found
                tempLabelMap.put(matcher.group(1), l.line);
                line.text = l.text.substring(matcher.group(1).length()+1).trim();
            } else {
                line.text = l.text;
            }
            noLabels.add(line);
        }

        // remove whitespace yet again
        List<Line> noWhiteSpace = noLabels
                .stream()
                .filter(l -> l.text.length() > 0)
                .collect(Collectors.toList());

        // expand pseudoinstructions
        List<Line> expandedPseudoInstructions = new ArrayList<Line>();
        for (Line l : noWhiteSpace) {
            String instructionName = l.text.split("\\s+")[0];
            if (pseudoInstructionGrammarMap.containsKey(instructionName)) {
                PseudoInstructionGrammar pseudoInstructionGrammar = pseudoInstructionGrammarMap.get(instructionName);
                pattern = Pattern.compile(pseudoInstructionGrammar.regex);
                Matcher matcher = pattern.matcher(l.text.substring(instructionName.length()));
                if (matcher.find()) {
                    List<String> subs = new ArrayList<String>();
                    for (int i=0; i<matcher.groupCount(); i++) {
                        subs.add(matcher.group(i+1));
                    }
                    Pattern resultPattern = Pattern.compile("[^%]*%(\\d+).*");
                    for (String newInstruction : pseudoInstructionGrammar.results) {
                        String temp = newInstruction;
                        Matcher m;
                        while ((m = resultPattern.matcher(temp)).find()) {
                            int desiredIndex = Integer.parseInt(m.group(1));
                            temp = temp.replace("%" + desiredIndex, subs.get(desiredIndex - 1));
                        }
                        Line newLine = new Line();
                        newLine.line = l.line;
                        newLine.text = temp;
                        expandedPseudoInstructions.add(newLine);
                    }
                } else {
                    errors.add(l.line +"::pseudoinstruction expansion failed");
                }
            } else {
                expandedPseudoInstructions.add(l);
            }
        }

        // assign instruction numbers
        for (int i=0; i<expandedPseudoInstructions.size(); i++) {
            expandedPseudoInstructions.get(i).instructionLine = i;
        }

        Map<String, Integer> labelMap = new HashMap<String, Integer>();
        // assign label line numbers
        for (String label : tempLabelMap.keySet()) {
            // iterate through all lines
            for (Line l : expandedPseudoInstructions) {
                if (l.line >= tempLabelMap.get(label)) {
                    labelMap.put(label, l.instructionLine);
                    // only want first line so pseudo instructions still work
                    break;
                }
            }
        }

        // process instructions
        for (Line l : expandedPseudoInstructions) {
            String[] stringArr = l.text.split("\\s+");
            String cutText = l.text.substring(stringArr[0].length());
            if (!instructionGrammarMap.containsKey(stringArr[0])) {
                errors.add(l.line + "::error: unknown instruction: "+stringArr[0]);
                continue;
            }

            InstructionGrammar instructionGrammar = instructionGrammarMap.get(stringArr[0]);

            // compile the regex group capture
            pattern = Pattern.compile(instructionGrammar.regex);

            // attempt to match the groups
            Matcher matcher = pattern.matcher(cutText);

            int binary = 0;

            if (matcher.find()) {
                for (int i = 0; i < instructionGrammar.vars.size(); i++) {
                    SingleVar var = instructionGrammar.vars.get(i);
                    String stringValue = matcher.group(i + 1);

                    int numericValue = 0;
                    try {
                        // is numeric
                        numericValue = Integer.decode(stringValue);
                        if (var.isBranch) {
                            numericValue = numericValue - l.instructionLine - 1;
                        }
                    } catch (Exception e) {
                        // is in data
                        if (dataVarMap.containsKey(stringValue)) {
                            if (var.isBranch) {
                                numericValue = dataVarMap.get(stringValue) - l.instructionLine - 1;
                            } else {
                                numericValue = dataVarMap.get(stringValue);
                            }
                        } else if (labelMap.containsKey(stringValue)) {
                            if (var.isBranch) {
                                numericValue = labelMap.get(stringValue) - l.instructionLine - 1;
                            } else {
                                numericValue = labelMap.get(stringValue);
                            }
                        } else {
                            errors.add(l.line + "::error: non-numeric value not found in data: " + stringValue);
                        }
                    }

                    if (numericValue >= 0) {
                        long maxValueByBits = pow(2, var.numBits - 1) - 1;
                        if (numericValue > maxValueByBits) {
                            // error
                            errors.add("error: value (" + numericValue + ") exceeds max possible value determined by bits (" + maxValueByBits + ")");
                        }
                    } else {
                        long minValueByBits = -pow(2, var.numBits);
                        if (numericValue < minValueByBits) {
                            // error
                            errors.add("error: value (" + numericValue + ") is less than smallest possible value determined by bits (" + minValueByBits + ")");
                        }
                    }

                    int valueToShiftAndAdd = (int) ((pow(2,var.numBits + 1) - 1) & numericValue);
                    int valueToAdd = valueToShiftAndAdd << var.index;
                    binary += valueToAdd;
                }

                // add all the opcodes
                List<OpCode> opCodes = instructionGrammar.opCodes;
                binary += opCodes.stream().mapToInt(op -> op.opCode << op.index).sum();

            } else {
                errors.add(l.line+"::error: regex failed to match for insn " + stringArr[0]);
            }
            binaryOutput.add(String.format("%32s", Integer.toBinaryString(binary)).replaceAll(" ", "0"));
        }

        return errors.size() == 0;
    }

    /***
     * Retrieves the processed ISA
     * @return
     */
    public List<String> getBinary() {
        return binaryOutput;
    }

    private long pow(long a, int b) {
        if (b == 0) return 1;
        if (b == 1) return a;
        if (b % 2 == 0) return pow(a * a, b / 2);
        else return a * pow(a * a, b / 2);

    }
}
