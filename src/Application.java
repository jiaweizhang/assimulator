import assemble.Assembler;
import assemble.ECE350Assembler;
import disassemble.Disassembler;
import disassemble.ECE350Disassembler;
import io.Reader;
import io.Writer;
import models.ECE350State;
import models.IntLine;
import models.StringLine;
import simulate.ECE350Simulator;
import simulate.Simulator;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {
    public static void main(String args[]) {
        // System.out.println(System.getProperty("user.dir"));
        Application a = new Application();
        //a.assemble();
        //a.disassemble();
        a.simulate();
    }

    private void assemble() {
        Reader r = new Reader();
        List<String> strings = null;
        try {
            strings = r.readFile("asm/test.asm");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(strings);
        List<String> readableStrings = a.toString(ints);
        List<String> binaryStrings = a.toBinary(ints);

        Writer w = new Writer();
        w.toConsole(readableStrings);
        w.toMifConsole(binaryStrings);
    }

    private void disassemble() {
        Reader r = new Reader();
        List<String> strings = null;
        try {
            strings = r.readFile("mif/test.mif");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Disassembler d = new ECE350Disassembler();
        List<StringLine> parsed = d.parse(strings);
        List<String> readable = d.toString(parsed);

        Writer w = new Writer();
        w.toConsole(readable);
        w.toAsmConsole(readable);
    }

    private void simulate() {
        Reader r = new Reader();
        List<String> strings = null;
        try {
            strings = r.readFile("asm/test.asm");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        int instructions = 50;

        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(strings);

        Simulator s = new ECE350Simulator();
        List<ECE350State> states = s.simulate(instructions, ints, new HashMap<Integer, Integer>());
    }
}
