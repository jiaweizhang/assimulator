package models;

/**
 * Created by jiaweizhang on 4/8/16.
 */
public class IntLine {
    private int integer;
    private int line;

    public IntLine(int line, int integer) {
        this.line = line;
        this.integer = integer;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
