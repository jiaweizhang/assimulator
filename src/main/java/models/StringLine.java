package models;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class StringLine {
    private String string;
    private int line;

    public StringLine(int line, String string) {
        this.line = line;
        this.string = string;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
