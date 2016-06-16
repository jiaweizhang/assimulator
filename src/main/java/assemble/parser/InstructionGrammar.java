package assemble.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class InstructionGrammar {
    public String name;
    public String regex;
    public List<SingleVar> vars;
    public List<OpCode> opCodes;
    public List<String> errors;

    public InstructionGrammar() {
        errors = new ArrayList<String>();
    }

    public String process(String insn) {
        // remove the name
        String[] stringArr = insn.split("\\s+");
        insn = insn.substring(stringArr[0].length());

        // compile the regex group capture
        Pattern pattern = Pattern.compile(regex);

        // attempt to match the groups
        Matcher matcher = pattern.matcher(insn);

        int binary = 0;

        if (matcher.find()) {
            for (int i = 0; i < vars.size(); i++) {
                String stringValue = matcher.group(i + 1);

                int numericValue = 0; // TODO remove default value
                if (stringValue.matches("^-?\\d+$")) {
                    // is numeric
                    numericValue = Integer.parseInt(stringValue);
                } else {
                    // error
                    errors.add("error: non-numeric value: " + stringValue);
                }

                if (numericValue >= 0) {
                    long maxValueByBits = pow(2, vars.get(i).numBits - 1) - 1;
                    if (numericValue > maxValueByBits) {
                        // error
                        errors.add("error: value (" + numericValue + ") exceeds max possible value determined by bits (" + maxValueByBits + ")");
                    }
                } else {
                    long minValueByBits = -pow(2, vars.get(i).numBits);
                    if (numericValue < minValueByBits) {
                        // error
                        errors.add("error: value (" + numericValue + ") is less than smallest possible value determined by bits (" + minValueByBits + ")");
                    }
                }

                int valueToShiftAndAdd = (int) ((pow(2, vars.get(i).numBits + 1) - 1) & numericValue);
                int valueToAdd = valueToShiftAndAdd << vars.get(i).insnIndex;
                binary += valueToAdd;
            }

            // add all the opcodes
            binary += opCodes.stream().mapToInt(op -> op.opCode << op.insnIndex).sum();

        } else {
            errors.add("error: regex failed to match");
        }
        return String.format("%32s", Integer.toBinaryString(binary)).replaceAll(" ", "0");
    }

    private long pow(long a, int b) {
        if (b == 0) return 1;
        if (b == 1) return a;
        if (b % 2 == 0) return pow(a * a, b / 2);
        else return a * pow(a * a, b / 2);

    }
}
