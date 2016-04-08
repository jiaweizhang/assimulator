package disassemble;

import models.StringLine;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public interface Disassembler {
    public List<StringLine> parse(List<String> input);

    public List<String> toString(List<StringLine> input);
}
