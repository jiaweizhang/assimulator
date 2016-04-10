package io;

import models.ECE350State;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Stringer {

    public String toMif(List<String> input) {
        StringBuilder sb = new StringBuilder();
        sb.append("DEPTH = 4096;\n");
        sb.append("WIDTH = 32;\n");
        sb.append("ADDRESS_RADIX = DEC;\n");
        sb.append("DATA_RADIX = BIN;\n");
        sb.append("CONTENT\n");
        sb.append("BEGIN\n");
        sb.append("\n");

        for (int i = 0; i < input.size(); i++) {
            sb.append(i + " : " + input.get(i) + ";\n");
        }

        sb.append("\n");
        sb.append("END;\n");
        return sb.toString();
    }

    public String toAsm(List<String> input) {
        StringBuilder sb = new StringBuilder();
        for (String str : input) {
            sb.append(str + "\n");
        }
        return sb.toString();
    }

    public String toStates(List<ECE350State> states) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < states.size(); i++) {
            sb.append("Number: " + i + "\n");
            sb.append(states.get(i).toString());
        }
        return sb.toString();
    }
}
