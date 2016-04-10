package assemble;

import models.IntLine;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */

public interface Assembler {
    public List<String> toBinary(List<IntLine> input);

    public List<IntLine> parse(List<String> input);

    public List<String> toString(List<IntLine> input);
}
