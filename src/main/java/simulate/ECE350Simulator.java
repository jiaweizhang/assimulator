package simulate;

import models.ECE350State;
import models.IntLine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiaweizhang on 4/9/16.
 */
public class ECE350Simulator implements Simulator {
    private int current;
    private int[] registers;
    private Map<Integer, Integer> dmem;
    private List<ECE350State> states;

    public List<ECE350State> simulate(int numberToSimulate, List<IntLine> input) {
        return simulate(numberToSimulate, input, new HashMap<Integer, Integer>());
    }

    @Override
    public List<ECE350State> simulate(int numberToSimulate, List<IntLine> input, HashMap<Integer, Integer> dmem) {
        // initialize
        current = 0;
        registers = new int[32];
        this.dmem = dmem;
        states = new ArrayList<ECE350State>();

        // IntLine to int
        List<Integer> justInts = new ArrayList<Integer>();
        for (IntLine il : input) {
            justInts.add(il.getInteger());
        }

        // loop
        for (int i = 0; i < numberToSimulate; i++) {
            if (current >= input.size()) {
                printError("Simulation finished because PC is out of bounds");
                return states;
            }
            simulateSingle(justInts.get(current), i + 1);
            addToStates();
        }
        System.out.println("Simulation Finished");
        return states;
    }

    private void addToStates() {
        int[] newRegisters = new int[32];
        for (int i = 0; i < 32; i++) {
            newRegisters[i] = registers[i];
        }
        Map<Integer, Integer> newDmem = new HashMap<Integer, Integer>(dmem);
        states.add(new ECE350State(newRegisters, newDmem));
    }

    private void simulateSingle(Integer insn, int index) {
        System.out.println(String.format("%4d: ", index) + "Simulating instruction: " + String.format("%32s", Integer.toBinaryString(insn)).replaceAll(" ", "0") + " : " + printInsn(insn));
        int opcode = (insn >> 27) & 31;
        switch (opcode) {
            case 0:
                int aluOp = (insn >> 2) & 31;
                switch (aluOp) {
                    case 0:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] + registers[(insn >> 12) & 31];
                        current++;
                        return;
                    case 1:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] - registers[(insn >> 12) & 31];
                        current++;
                        return;
                    case 2:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] & registers[(insn >> 12) & 31];
                        current++;
                        return;
                    case 3:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] | registers[(insn >> 12) & 31];
                        current++;
                        return;
                    case 4:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] << ((insn >> 7) & 31);
                        current++;
                        return;
                    case 5:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] >> ((insn >> 7) & 31);
                        current++;
                        return;
                    case 6:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] * registers[(insn >> 12) & 31];
                        current++;
                        return;
                    case 7:
                        registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] * registers[(insn >> 12) & 31];
                        current++;
                        return;
                    default:
                        printError("Unrecognized alu op: " + aluOp);
                        current++;
                        return;
                }
            case 1:
                current = (insn & 134217727);
                return;
            case 2:
                if (registers[((insn >> 22) & 31)] != registers[((insn >> 17) & 31)]) {
                    current = current + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071)) + 1;
                } else {
                    current++;
                }
                return;
            case 3:
                registers[31] = current + 1;
                current = (insn & 134217727);
                return;
            case 4:
                current = registers[(insn >> 22) & 31];
                return;
            case 5:
                registers[(insn >> 22) & 31] = registers[(insn >> 17) & 31] + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
                current++;
                return;
            case 6:
                if (registers[((insn >> 22) & 31)] != registers[((insn >> 17) & 31)]) {
                    current = current + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071)) + 1;
                } else {
                    current++;
                }
                return;
            case 7:
                int address = registers[(insn >> 17) & 31] + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
                int data = registers[(insn >> 22) & 31];
                if (data == 0) {
                    if (dmem.containsKey(address)) {
                        if (dmem.get(address) == 0) {
                            // don't do anything
                        } else {
                            dmem.remove(address);
                        }
                    }
                } else {
                    dmem.put(address, data);
                }
                current++;
                return;
            case 8:
                int loadAddress = registers[(insn >> 17) & 31] + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
                int loadReg = (insn >> 22) & 31;
                if (dmem.containsKey(loadAddress)) {
                    registers[loadReg] = dmem.get(loadAddress);
                } else {
                    registers[loadReg] = 0;
                }
                current++;
                return;
            case 21:
                printError("setx not implemented");
                current++;
                return;
            //return "setx " + (insn & 134217727);
            case 22:
                printError("bex not implemented");
                current++;
                return;
            //return "bex " + (insn & 134217727);
            default:
                printError("Unrecognized opcode: " + opcode);
                current++;
                return;
        }
    }

    private void printError(String message) {
        System.out.println("Simulation Error: " + message);
    }

    private String printInsn(int insn) {
        int opcode = (insn >> 27) & 31;
        switch (opcode) {
            case 0:
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
                        return "sll $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + ((insn >> 7) & 31);
                    case 5:
                        return "sra $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + ((insn >> 7) & 31);
                    case 6:
                        return "mul $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
                    case 7:
                        return "div $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", $r" + ((insn >> 12) & 31);
                    default:
                        printError("Unrecognized alu op: " + aluOp);
                        return "noop";
                }
            case 1:
                return "j " + (insn & 134217727);
            case 2:
                return "bne $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
            case 3:
                return "jal " + (insn & 134217727);
            case 4:
                return "jr $r" + ((insn >> 22) & 31);
            case 5:
                return "addi $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
            case 6:
                return "blt $r" + ((insn >> 22) & 31) + ", $r" + ((insn >> 17) & 31) + ", " + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071));
            case 7:
                return "sw $r" + ((insn >> 22) & 31) + ", " + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071)) + "($r" + ((insn >> 17) & 31) + ")";
            case 8:
                return "lw $r" + ((insn >> 22) & 31) + ", " + (((insn >> 16) & 1) == 1 ? (insn & 131071) - 131072 : (insn & 131071)) + "($r" + ((insn >> 17) & 31) + ")";
            case 21:
                return "setx " + (insn & 134217727);
            case 22:
                return "bex " + (insn & 134217727);
            default:
                printError("Unrecognized opcode: " + opcode);
                return "noop";
        }
    }
}
