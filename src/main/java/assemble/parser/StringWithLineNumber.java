package assemble.parser;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class StringWithLineNumber {
    public int line;
    public String string;

    public StringWithLineNumber (int line, String string) {
        this.line = line;
        this.string = string;
    }

    public StringWithLineNumber removeComments() {
        int commentIndex;
        if ((commentIndex = string.indexOf(";")) != -1) {
            // comment exists
            string = string.substring(0, commentIndex).trim();
        }
        return new StringWithLineNumber(line, string.trim());
    }

    public StringWithLineNumber removeHeader() {
        if (string.indexOf(".text") == 0 || string.indexOf(".data") == 0) {
            return new StringWithLineNumber(line, string.substring(5).trim());
        }
        return this;
    }
}