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
import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static spark.Spark.*;


/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {

    private static final HashMap<String, String> corsHeaders = new HashMap<String, String>();

    static {
        corsHeaders.put("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE,OPTIONS");
        corsHeaders.put("Access-Control-Allow-Origin", "*");
        corsHeaders.put("Access-Control-Allow-Headers", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin,");
        corsHeaders.put("Access-Control-Allow-Credentials", "true");
    }

    private final static void apply() {
        Filter filter = new Filter() {
            @Override
            public void handle(Request request, Response response) throws Exception {
                corsHeaders.forEach((key, value) -> {
                    response.header(key, value);
                });
            }
        };
        Spark.after(filter);
    }

    private static String getStaticFileLocation() {
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("config.properties");

            prop.load(input);

            return prop.getProperty("location");

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // TODO
        return "";
    }

    public static void main(String args[]) {
        port(80);
        //staticFileLocation("/public");
        System.out.println(getStaticFileLocation());
        externalStaticFileLocation(getStaticFileLocation());

        apply();

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
