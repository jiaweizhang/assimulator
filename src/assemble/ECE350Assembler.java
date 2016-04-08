package assemble;

import models.IntLine;
import models.StringLine;

import java.util.*;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class ECE350Assembler implements Assembler {
    Map<String, Integer> labelMap = new HashMap<String, Integer>();
    Map<String, Integer> dmemMap = new HashMap<String, Integer>();

    @Override
    public List<String> toBinary(List<IntLine> input) {
        List<String> strings = new ArrayList<String>();
        for (IntLine i : input) {
            strings.add(String.format("%32s", Integer.toBinaryString(i.getInteger())).replaceAll(" ", "0"));
        }
        return strings;
    }

    @Override
    public List<String> toString(List<IntLine> input) {
        List<String> strings = new ArrayList<String>();
        for (IntLine i : input) {
            strings.add(String.format("%4d: ", i.getLine()) + String.format("%32s", Integer.toBinaryString(i.getInteger())).replaceAll(" ", "0"));
        }
        return strings;
    }

    @Override
    public List<IntLine> parse(List<StringLine> input) {
        Set<String> insns = new HashSet<String>();
        insns.addAll(Arrays.asList("add", "addi", "sub", "and", "or", "sll", "sra", "mul", "div", "j", "bne", "jal", "jr", "blt", "bex", "setx", "sw", "lw"));
        List<StringLine> unLabeled = new ArrayList<>();
        int insnCount = 0;

        for (StringLine sl : input) {
            String str = sl.getString();
            String[] arr = str.split("[, \\(\\)]+");
            if (!insns.contains(arr[0])) {
                if (arr[0].charAt(arr[0].length() - 1) != ':') {
                    printError(sl.getLine(), "Unrecognized symbol: " + arr[0]);
                    return new ArrayList<IntLine>();
                } else {
                    labelMap.put(arr[0].substring(0, arr[0].length() - 1), insnCount);
                    //System.out.println(arr[0] + " : "+insnCount);
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
            String[] arr = str.split("[, \\(\\)]+");
            Integer parsedSingle = parseSingle(sl.getLine(), arr, insnNumber);
            parsed.add(new IntLine(sl.getLine(), parsedSingle));
            insnNumber++;
        }
        return parsed;
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
                bin = (5 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (se(line, arr[3]));
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
                bin = (5 << 27) + (reg(line, arr[1]) << 22) + (reg(line, arr[2]) << 17) + (seLabel(line, arr[3], currentLine));
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
            default:
                printError(line, "Unknown symbol: "+arr[0]);
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
        int n = 0;
        if (!num.matches("\\d+")) {
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
        int n = 0;
        if (!num.matches("\\d+")) {
            if (labelMap.containsKey(num)) {
                int desiredLine = labelMap.get(num);
                n = desiredLine - currentLine - 1;
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
        int n = Integer.parseInt(bigN);
        if (n < 0 | n > 134217727) {
            printError(line, "N must be between 0 and 2^27-1");
            return 0;
        }
        return n;
    }

    private int smt(int line, String amt) {
        int n = Integer.parseInt(amt);
        if (n < 0 | n > 31) {
            printError(line, "Shamt must be between 0 and 31");
            return 0;
        }
        return n;
    }

    private int reg(int line, String input) {
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
        System.out.println("Assemble Error - Line " + line + ": " + message);
    }
}
