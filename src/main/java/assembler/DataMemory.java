package assembler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaweizhang on 10/31/2016.
 */
public class DataMemory {
    public List<Integer> dataMemory;
    public int dataAddress;

    public DataMemory() {
        dataMemory = new ArrayList<Integer>();
        dataAddress = 0;
    }

    public int addData(List<Integer> data) {
        dataAddress += data.size();
        dataMemory.addAll(data);
        return dataAddress;
    }
}
