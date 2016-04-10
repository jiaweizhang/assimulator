package models;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by jiaweizhang on 4/9/16.
 */
public class ECE350State {
    private int[] register;
    private Map<Integer, Integer> dmem;

    public ECE350State(int[] register, Map<Integer, Integer> dmem) {
        this.register = register;
        this.dmem = dmem;
    }

    public int[] getRegister() {
        return register;
    }

    public void setRegister(int[] register) {
        this.register = register;
    }

    public Map<Integer, Integer> getDmem() {
        return dmem;
    }

    public void setDmem(Map<Integer, Integer> dmem) {
        this.dmem = dmem;
    }

    @Override
    public String toString() {
        return Arrays.toString(register) + "\n" + dmem.toString() + "\n";
    }
}
