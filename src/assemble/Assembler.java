package assemble;

import models.IntLine;
import models.StringLine;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */

public interface Assembler {
    public List<String> toBinary(List<IntLine> input);

    public List<IntLine> parse(List<StringLine> input);

    public List<String> toString(List<IntLine> input);
}
