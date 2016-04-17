import assemble.Assembler;
import assemble.AssemblerService;
import assemble.ECE350Assembler;
import auth.AuthService;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import disassemble.DisassemblerService;
import io.Stringer;
import models.ECE350State;
import models.IntLine;
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
        String staticFileLocation = System.getProperty("user.dir") + "/webapp";
        externalStaticFileLocation(staticFileLocation);

        apply();

        DB mongo = null;
        try {
            mongo = mongo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        AuthService as = new AuthService(mongo);

        /*
        before("/api/protected/*", (req, res) -> {
            if (!as.verify(req)) {
                halt(401, "Not authorized");
            }
        });
        */

        new AssemblerService(mongo);

        new DisassemblerService(mongo);

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

    private String simulate(List<String> strings) {
        // TODO allow variable
        int instructions = 50;

        Assembler a = new ECE350Assembler();
        a.parse(strings);
        List<IntLine> ints = a.getImem();

        Simulator s = new ECE350Simulator();
        List<ECE350State> states = s.simulate(instructions, ints, new HashMap<Integer, Integer>());

        Stringer w = new Stringer();
        return w.toStates(states);
    }
}
