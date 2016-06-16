package assemble.parser;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class OpCode {
    public int opCode;
    public int insnIndex;

    public OpCode (int opCode, int insnIndex) {
        this.opCode = opCode;
        this.insnIndex = insnIndex;
    }
}
