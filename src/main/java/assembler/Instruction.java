package assembler;

import static assembler.Instruction.InstructionType.R;

/**
 * Created by jiaweizhang on 11/3/2016.
 */
public class Instruction {
    public enum InstructionType {
        R,
        I,
        JI,
        JII
    }

    public InstructionType instructionType;


    public class RInstruction extends Instruction {

        public RInstruction () {
            this.instructionType = R;
        }
    }

    public class IInstruction extends Instruction {

        public IInstruction () {
            this.instructionType = I;
        }
    }

    public class JIInstruction extends Instruction {

        public JIInstruction () {
            this.instructionType = JI;
        }
    }
}