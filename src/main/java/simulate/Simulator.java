package simulate;

import models.ECE350State;
import models.IntLine;

import java.util.HashMap;
import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public interface Simulator {
    public List<ECE350State> simulate(int numberToSimulate, List<IntLine> input, HashMap<Integer, Integer> dmem);
}
