package assemble;

import models.IntLine;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */

public interface Assembler {
    public List<String> toDmemBinary();

    public List<String> toImemBinary();

    public void parse(List<String> input);

    public List<IntLine> getImem();

}
