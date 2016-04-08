package disassemble;

import disassemble.numberparse.BinParser;
import disassemble.numberparse.DecParser;
import disassemble.numberparse.NumberParser;
import models.StringLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaweizhang on 4/8/16.
 */
public class ECE350Disassembler implements Disassembler {
    private int depth;
    private int width;
    private String addressRadix;
    private String dataRadix;

    @Override
    public List<StringLine> parse(List<String> input) {
        List<StringLine> withLines = new ArrayList<StringLine>();
        for (int i=0; i<input.size(); i++) {
            String trimmed = input.get(i).trim();
            if (trimmed.length() > 0) {
                withLines.add(new StringLine(i, trimmed));
            }
        }

        List<String> headers = new ArrayList<String>();
        int unfulfilled = 5;
        while (unfulfilled > 0) {
            StringLine frontSL = withLines.get(0);
            String front = frontSL.getString();
            if (front.indexOf("DEPTH") != -1 ) {
                depth = Integer.parseInt(front.split("[ :=;]+")[1]);
            } else if (front.indexOf("WIDTH") != -1) {
                width = Integer.parseInt(front.split("[ :=;]+")[1]);
            } else if (front.indexOf("ADDRESS_RADIX") != -1) {
                addressRadix = front.split("[ :;=]+")[1];
            } else if (front.indexOf("DATA_RADIX") != -1) {
                dataRadix = front.split("[ :;=]+")[1];
            } else if (front.indexOf("CONTENT") != -1) {
                // nothing
                if (withLines.get(1).getString().indexOf("BEGIN") != -1) {
                    withLines.remove(0);
                }
            } else {
                printError(frontSL.getLine(), "Unrecognized symbol: " + front);
                break;
            }
            withLines.remove(0);
            unfulfilled--;
        }

        // create readers
        NumberParser ap = null;
        if (addressRadix.equals("DEC")) {
            ap = new DecParser();
        } else {
            printError(0, "No parser for address radix " + addressRadix);
            return new ArrayList<StringLine>();
        }

        NumberParser dp = null;
        if (dataRadix.equals("BIN")) {
            dp = new BinParser();
        } else {
            printError(0, "No parser for data radix " + dataRadix);
            return new ArrayList<StringLine>();
        }

        if (withLines.get(withLines.size()-1).getString().indexOf("END") == -1) {
            printError(0, "No END data symbol detected");
            return new ArrayList<StringLine>();
        }

        List<StringLine> asm = new ArrayList<StringLine>();

        while (withLines.get(0).getString().indexOf("END") == -1) {
            StringLine first = withLines.remove(0);
            String[] arr = first.getString().split("[ :;]+");
            Integer insn = dp.parse(arr[1]);
            asm.add(new StringLine(first.getLine(), mapSingle(first.getLine(), insn)));
        }
        return asm;
    }

    private String mapSingle(int line, Integer insn) {
        int opcode = (insn >> 27) & 31;
        switch (opcode) {
            case 0:
                return rType(line, insn);
            case 1:
                return "j " + (insn & 134217727);
            case 2:
                return "bne $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1? (insn & 131071) - 131072 : (insn & 131071));
            case 3:
                return "jal " + (insn & 134217727);
            case 4:
                return "jr $r" + ((insn >> 22) & 31);
            case 5:
                return "addi $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1? (insn & 131071) - 131072 : (insn & 131071));
            case 6:
                return "blt $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1? (insn & 131071) - 131072 : (insn & 131071));
            case 7:
                return "sw $r" + ((insn >> 22) & 31) + ", " + (((insn >> 16) & 1) == 1? (insn & 131071) - 131072 : (insn & 131071)) + "($r" + ((insn >> 17) & 31) + ")";
            case 8:
                return "lw $r" + ((insn >> 22) & 31) + ", " + (((insn >> 16) & 1) == 1? (insn & 131071) - 131072 : (insn & 131071)) + "($r" + ((insn >> 17) & 31) + ")";
            case 21:
                return "setx " + (insn & 134217727);
            case 22:
                return "bex " + (insn & 134217727);
            default:
                printError(line, "Unrecognized opcode: " + opcode);
                return "noop";
        }
    }

    private String rType(int line, int insn) {
        int aluOp = (insn >> 2) & 31;
        switch (aluOp) {
            case 0:
                return "add $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            case 1:
                return "sub $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            case 2:
                return "and $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            case 3:
                return "or $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            case 4:
                return "sll $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + ((insn >>7) & 31);
            case 5:
                return "sra $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + ((insn >>7) & 31);
            case 6:
                return "mul $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            case 7:
                return "div $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
            default:
                printError(line, "Unrecognized alu op: " + aluOp);
                return "noop";
        }
    }

    @Override
    public List<String> toString(List<StringLine> input) {
        List<String> output = new ArrayList<String>();
        for (StringLine sl : input) {
            output.add(sl.getString());
        }
        return output;
    }

    private void printError(int line, String message) {
        System.out.println("Disassemble Error - Line " + line + ": " + message);
    }
}
