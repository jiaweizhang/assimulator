import assemble.Assembler;
import assemble.AssemblerService;
import assemble.ECE350Assembler;
import com.mongodb.DB;
import com.mongodb.MongoClient;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;


/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {

    private static final boolean production = false;

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

    public static void main(String args[]) {
        if (production) {
            port(80);
        } else {
            port(8080);
        }

        System.out.println(System.getProperty("user.dir"));
        String staticFileLocation = System.getProperty("user.dir") + "/src/main/resources/public";
        externalStaticFileLocation(staticFileLocation);

        apply();

        DB mongo = null;
        try {
            mongo = mongo();
        } catch (Exception e) {
            e.printStackTrace();
        }
        new AssemblerService(mongo);

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

    private static DB mongo() throws Exception {
        MongoClient mongoClient = new MongoClient();
        DB db = mongoClient.getDB("test");
        return db;
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
