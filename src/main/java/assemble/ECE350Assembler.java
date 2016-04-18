package assemble;

import models.IntLine;
import models.StringLine;

import java.util.*;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class ECE350Assembler implements Assembler {
    private Map<String, Integer> labelMap = new HashMap<String, Integer>();
    private List<Integer> dmem = new ArrayList<Integer>();
    private List<IntLine> imem = new ArrayList<IntLine>();
    private Map<String, Integer> dmemMap = new HashMap<String, Integer>();
    private int dmemIndex = 0;
    private List<String> errors = new ArrayList<String>();

    public List<IntLine> getImem() {
        return imem;
    }

    /**
     * Wonder if compiler parallelizes these operations
     *
     * @param input
     * @return
     */
    public List<StringLine> asmSanitize(List<String> input) {
        List<StringLine> lined = addLines(input);
        List<StringLine> noComments = removeComments(lined);
        List<StringLine> trimmed = trimWhitespace(noComments);
        return trimmed;
    }

    /**
     * adds line number
     */
    private List<StringLine> addLines(List<String> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (int i = 0; i < input.size(); i++) {
            output.add(new StringLine(i + 1, input.get(i)));
        }
        return output;
    }

    /**
     * Not sure how optimal performance is
     * Removes whitespace from front and back and empty lines
     */
    private List<StringLine> trimWhitespace(List<StringLine> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (StringLine line : input) {
            String trimmed = line.getString().trim();
            if (trimmed.length() > 0) {
                output.add(new StringLine(line.getLine(), trimmed));
            }
        }
        return output;
    }

    /**
     * Removes comments
     *
     * @param input
     * @return
     */
    private List<StringLine> removeComments(List<StringLine> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (StringLine line : input) {
            if (line.getString().indexOf('#') != -1) {
                output.add(new StringLine(line.getLine(), line.getString().substring(0, line.getString().indexOf('#'))));
            } else {
                output.add(line);
            }
        }
        return output;
    }

    @Override
    public List<String> toDmemBinary() {
        List<String> strings = new ArrayList<String>();
        for (int i : dmem) {
            strings.add(String.format("%32s", Integer.toBinaryString(i)).replaceAll(" ", "0"));
        }
        return strings;
    }

    @Override
    public List<String> toImemBinary() {
        List<String> strings = new ArrayList<String>();
        for (IntLine i : imem) {
            strings.add(String.format("%32s", Integer.toBinaryString(i.getInteger())).replaceAll(" ", "0"));
        }
        return strings;
    }

    private void processData(List<StringLine> input) {
        for (StringLine sl : input) {
            String[] arr = sl.getString().split("\\s+");
            String label = arr[0].substring(0, arr[0].length() - 1);
            String type = arr[1];
            String value = arr[2];
            if (type.equals(".word")) {
                if (value.startsWith("0x")) {
                    // TODO
                    int unHexed = Integer.parseInt(value.substring(2), 16);
                    dmem.add(unHexed);
                } else {
                    // is alphanumeric
                    dmem.add(Integer.parseInt(value));
                }
                dmemMap.put(label, dmemIndex);
                dmemIndex++;
            } else if (type.equals(".string")) {
                dmemMap.put(label, dmemIndex);
                for (int i = 0; i < value.length(); i++) {
                    int c = (char) value.charAt(i);
                    dmem.add(c);
                    dmemIndex++;
                }
            } else if (type.equals(".char")) {
                int c = (char) value.charAt(0);
                dmem.add(c);
                dmemMap.put(label, dmemIndex);
                dmemIndex++;
            } else {
                printError(sl.getLine(), "Unrecognized type " + type);
                return;
            }
        }
    }

    private List<StringLine> getInstructions(List<StringLine> input) {
        int dotText = -1;
        int dotData = -1;
        int dotCustom = -1; // TODO
        for (int i = 0; i < input.size(); i++) {
            if (input.get(i).getString().contains(".text")) {
                dotText = i;
            } else if (input.get(i).getString().contains(".data")) {
                dotData = i;
            }
        }
        if (dotText == -1) {
            printError(0, ".text not found - invalid");
            return new ArrayList<StringLine>();
        }
        if (dotData == -1) {
            // no .data but .text exists
            return input.subList(dotText + 1, input.size());
        }
        if (dotData <= dotText) {
            processData(input.subList(dotData + 1, dotText));
            return input.subList(dotText + 1, input.size());
        }
        processData(input.subList(dotData + 1, input.size()));
        return input.subList(dotText + 1, dotData);
    }

    @Override
    public void parse(List<String> input) {
        List<StringLine> sanitized = asmSanitize(input);
        List<StringLine> instructionWithNoDot = getInstructions(sanitized);

        Set<String> insns = new HashSet<String>();
        insns.addAll(Arrays.asList("add", "addi", "sub", "and", "or", "sll", "sra", "mul", "div", "j", "bne", "jal", "jr", "blt", "bex", "setx", "sw", "lw"));
        insns.addAll(Arrays.asList("beq", "swd", "tty", "bgt", "noop", "nop", "halt"));
        List<StringLine> unLabeled = new ArrayList<>();
        int insnCount = 0;

        for (StringLine sl : instructionWithNoDot) {
            String str = sl.getString();
            String[] arr = str.split("[\\s+,\\(\\)]+");
            if (!insns.contains(arr[0])) {
                if (arr[0].charAt(arr[0].length() - 1) != ':') {
                    printError(sl.getLine(), "Unrecognized symbol: " + arr[0]);
                    return;
                } else {
                    labelMap.put(arr[0].substring(0, arr[0].length() - 1), insnCount);
                    if (arr.length > 1) {
                        // stuff after label
                        StringLine newSL = new StringLine(sl.getLine(), sl.getString().substring(arr[0].length() + 1));
                        unLabeled.add(newSL);
                        insnCount++;
                    }
                }
            } else {
                unLabeled.add(sl);
                insnCount++;
            }
        }

        List<IntLine> parsed = new ArrayList<IntLine>();
        int insnNumber = 0;
        for (StringLine sl : unLabeled) {
            String str = sl.getString();
            String[] arr = str.split("[\\s+,\\(\\)]+");
            Integer parsedSingle = parseSingle(sl.getLine(), arr, insnNumber);
            parsed.add(new IntLine(sl.getLine(), parsedSingle));
            insnNumber++;
        }
        imem.addAll(parsed);
    }

    private Integer parseSingle(int line, String[] arr, int currentLine) {
        Integer bin = 0;
        switch (arr[0]) {
            case "add":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12);
                break;
            case "addi":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (5 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (seLabel(line, arr[3], currentLine));
                break;
            case "sub":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12) + (1 << 2);
                break;
            case "and":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12) + (2 << 2);
                break;
            case "or":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12) + (3 << 2);
                break;
            case "sll":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (smt(line, arr[3]) << 7) + (4 << 2);
                break;
            case "sra":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (smt(line, arr[3]) << 7) + (5 << 2);
                break;
            case "mul":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12) + (6 << 2);
                break;
            case "div":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (reg(line, arr[3]) << 12) + (7 << 2);
                break;
            case "j":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (1 << 27) + (bigNLabel(line, arr[1]));
                break;
            case "bne":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (2 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (seLabel(line, arr[3], currentLine));
                break;
            case "jal":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (3 << 27) + (bigNLabel(line, arr[1]));
                break;
            case "jr":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (4 << 27) + (reg(line, arr[1]) << 22);
                break;
            case "blt":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (6 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (seLabel(line, arr[3], currentLine));
                break;
            case "bex":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (22 << 27) + (bigNLabel(line, arr[1]));
                break;
            case "setx":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (21 << 27) + (bigN(line, arr[1]));
                break;
            case "sw":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (7 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[3]) << 17) + (seLabelDmem(line, arr[2]));
                break;
            case "lw":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (8 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[3]) << 17) + (seLabelDmem(line, arr[2]));
                break;
            case "noop":
                if (!checkArgs(line, arr[0], arr, 0)) break;
                bin = 0;
                break;
            case "nop":
                if (!checkArgs(line, arr[0], arr, 0)) break;
                bin = 0;
                break;
            case "halt":
                if (!checkArgs(line, arr[0], arr, 0)) break;
                bin = (1 << 27) + currentLine;
                break;
            case "beq":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (16 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (seLabel(line, arr[3], currentLine));
                break;
            case "swd":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (17 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[3]) << 17) + (seLabelDmem(line, arr[2]));
                break;
            case "tty":
                if (!checkArgs(line, arr[0], arr, 1)) break;
                bin = (30 << 27) + (reg(line, arr[1]) << 22);
                break;
            case "bgt":
                if (!checkArgs(line, arr[0], arr, 3)) break;
                bin = (6 << 27) + (reg(line, arr[1]) << 17) + (reg(line, arr[2]) << 22) + (seLabel(line, arr[3], currentLine));
                break;
            default:
                printError(line, "Unknown symbol: " + arr[0]);
                bin = 0;
                break;


        }
        return bin;
    }

    private boolean checkArgs(int line, String insn, String[] args, int expected) {
        if (args.length - 1 != expected) {
            printError(line, "Instruction " + insn + " expects " + expected + " arguments but has " + (args.length - 1) + "arguments.");
            return false;
        }
        return true;
    }

    private int seLabelDmem(int line, String num) {
        num = num.trim();
        int n = 0;
        if (!num.matches("-?\\d+")) {
            if (dmemMap.containsKey(num)) {
                n = dmemMap.get(num);
            } else {
                printError(line, "Unknown symbol: " + num);
            }
        } else {
            n = Integer.parseInt(num);
        }
        if (n < -65536 | n > 65535) {
            printError(line, "N must be between 2^-16 and 2^16-1");
            return 0;
        }
        if (n < 0) {
            // add 2^17
            return n + 131072;
        }
        return n;
    }

    private int seLabel(int line, String num, int currentLine) {
        num = num.trim();
        int n = 0;
        if (!num.matches("-?\\d+")) {
            if (labelMap.containsKey(num)) {
                int desiredLine = labelMap.get(num);
                n = desiredLine - currentLine - 1;
            } else if (num.startsWith("0x")) {
                n = Integer.parseInt(num.substring(2), 16);
            } else {
                printError(line, "Unknown symbol: " + num);
            }
        } else {
            n = Integer.parseInt(num);
        }
        if (n < -65536 | n > 65535) {
            printError(line, "N must be between 2^-16 and 2^16-1");
            return 0;
        }
        if (n < 0) {
            // add 2^17
            return n + 131072;
        }
        return n;
    }

    private int bigNLabel(int line, String bigN) {
        bigN = bigN.trim();
        int n = 0;
        if (!bigN.matches("\\d+")) {
            if (labelMap.containsKey(bigN)) {
                n = labelMap.get(bigN);
            } else {
                printError(line, "Unknown symbol: " + bigN);
            }
        } else {
            n = Integer.parseInt(bigN);
        }
        if (n < 0 | n > 134217727) {
            printError(line, "N must be between 0 and 2^27-1");
            return 0;
        }
        return n;
    }

    private int bigN(int line, String bigN) {
        bigN = bigN.trim();
        int n = Integer.parseInt(bigN);
        if (n < 0 | n > 134217727) {
            printError(line, "N must be between 0 and 2^27-1");
            return 0;
        }
        return n;
    }

    private int smt(int line, String amt) {
        amt = amt.trim();
        int n = Integer.parseInt(amt);
        if (n < 0 | n > 31) {
            printError(line, "Shamt must be between 0 and 31");
            return 0;
        }
        return n;
    }

    private int reg(int line, String input) {
        input = input.trim();
        if (input.equals("$ra")) {
            return 31;
        }
        if (input.equals("sp")) {
            return 30;
        }
        // not sure why OR regex isn't working but this works
        if (!input.matches("\\$(\\d)+") & !input.matches("\\$r(\\d)+")) {
            printError(line, "Register does not match format $(0-9)+ or $r(0-9)+");
            return 0;
        }
        String justNum = input.replaceAll("[^0-9]", "");
        int r = Integer.parseInt(justNum);
        if (r < 0 | r > 31) {
            printError(line, "Register value must be between 0 and 31");
            return 0;
        }
        return r;
    }

    private int se(int line, String num) {
        num = num.trim();
        int n = Integer.parseInt(num);
        if (n < -65536 | n > 65535) {
            printError(line, "N must be between 2^-16 and 2^16-1");
            return 0;
        }
        if (n < 0) {
            // add 2^17
            return n + 131072;
        }
        return n;
    }

    private void printError(int line, String message) {
        errors.add("Assemble Error - Line " + line + ": " + message);
        System.out.println("Assemble Error - Line " + line + ": " + message);
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public String getErrorString() {
        if (errors.size() == 0) {
            return "No errors";
        }
        StringBuilder sb = new StringBuilder();
        for (String error : errors) {
            sb.append(error + "\n");
        }
        return sb.toString();
    }
}
