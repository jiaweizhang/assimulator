package assembler;

/**
 * Created by jiaweizhang on 10/31/2016.
 */
public class Error {
    public Line line;
    public String message;

    public Error(Line line, String message) {
        this.line = line;
        this.message = message;
    }
}
