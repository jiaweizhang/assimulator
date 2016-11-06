package assimulator.assemble.parser;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class SingleVar {
    public int numBits;
    public int index;
    public boolean isBranch;

    public SingleVar(int numBits, int index, boolean isBranch) {
        this.numBits = numBits;
        this.index = index;
        this.isBranch = isBranch;
    }
}
