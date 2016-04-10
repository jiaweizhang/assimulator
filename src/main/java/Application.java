import assemble.Assembler;
import assemble.ECE350Assembler;
import disassemble.Disassembler;
import disassemble.ECE350Disassembler;
import io.Stringer;
import models.ECE350State;
import models.IntLine;
import models.StringLine;
import simulate.ECE350Simulator;
import simulate.Simulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.post;
import static spark.Spark.staticFileLocation;


/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {
    public static void main(String args[]) {
        staticFileLocation("/public");

        post("/api/assemble", (req, res) -> {
            Application a = new Application();
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            return a.assemble(list);
        });

        post("/api/disassemble", (req, res) -> {
            Application a = new Application();
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            return a.disassemble(list);
        });

        post("/api/simulate", (req, res) -> {
            Application a = new Application();
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            return a.simulate(list);
        });
    }

    private String assemble(List<String> strings) {
        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(strings);
        List<String> readableStrings = a.toString(ints);
        List<String> binaryStrings = a.toBinary(ints);

        Stringer w = new Stringer();
        return w.toMif(binaryStrings);
    }

    private String disassemble(List<String> strings) {
        Disassembler d = new ECE350Disassembler();
        List<StringLine> parsed = d.parse(strings);
        List<String> readable = d.toString(parsed);

        Stringer w = new Stringer();
        return w.toAsm(readable);
    }

    private String simulate(List<String> strings) {
        // TODO allow variable
        int instructions = 50;

        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(strings);

        Simulator s = new ECE350Simulator();
        List<ECE350State> states = s.simulate(instructions, ints, new HashMap<Integer, Integer>());

        Stringer w = new Stringer();
        return w.toStates(states);
    }
}
