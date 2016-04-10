package io;

import models.ECE350State;

import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Writer {

    public void toConsole(List<String> input) {
        for (String str : input) {
            System.out.println(str);
        }
    }

    public void toMifConsole(List<String> input) {
        System.out.println("DEPTH = 4096;");
        System.out.println("WIDTH = 32;");
        System.out.println("ADDRESS_RADIX = DEC;");
        System.out.println("DATA_RADIX = BIN;");
        System.out.println("CONTENT");
        System.out.println("BEGIN");
        System.out.println();

        for (int i=0; i<input.size(); i++) {
            System.out.println(i + " : " + input.get(i) + ";");
        }

        System.out.println();
        System.out.println("END;");
    }

    public void toAsmConsole(List<String> input) {
        // TODO
    }

    public void toStatesConsole(List<ECE350State> states) {
        for (int i=0; i<states.size(); i++) {
            System.out.println("Number: " + i);
            states.get(i).print();
        }
    }
}
