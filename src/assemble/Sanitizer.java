package assemble;

import models.StringLine;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Sanitizer {
    /**
     * Wonder if compiler parallelizes these operations
     *
     * @param input
     * @return
     */
    public List<StringLine> asmSanitize(List<String> input) {
        List<StringLine> lined = addLines(input);
        List<StringLine> noComments = removeComments(lined);
        List<StringLine> trimmed = trimWhitespace(noComments);
        return trimmed;
    }

    /**
     * adds line number
     */
    private List<StringLine> addLines(List<String> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (int i = 0; i < input.size(); i++) {
            output.add(new StringLine(i + 1, input.get(i)));
        }
        return output;
    }

    /**
     * Not sure how optimal performance is
     * Removes whitespace from front and back and empty lines
     */
    private List<StringLine> trimWhitespace(List<StringLine> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (StringLine line : input) {
            String trimmed = line.getString().trim();
            if (trimmed.length() > 0) {
                output.add(line);
            }
        }
        return output;
    }

    /**
     * Removes comments
     *
     * @param input
     * @return
     */
    private List<StringLine> removeComments(List<StringLine> input) {
        List<StringLine> output = new ArrayList<StringLine>();
        for (StringLine line : input) {
            if (line.getString().indexOf('#') != -1) {
                output.add(new StringLine(line.getLine(), line.getString().substring(0, line.getString().indexOf('#'))));
            } else {
                output.add(line);
            }
        }
        return output;
    }


}
