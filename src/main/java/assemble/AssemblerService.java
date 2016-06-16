package assemble;

import assemble.parser.InstructionGrammar;
import assemble.parser.InstructionGrammarData;
import assemble.parser.InstructionGrammarParser;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import db.JsonTransformer;
import io.Stringer;
import models.Asm;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by jiaweizhang on 4/11/16.
 */
public class AssemblerService {
    private final DBCollection assembler;

    public AssemblerService(DB db) {
        this.assembler = db.getCollection("assembler");
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/test", (req, res) -> {
            //List<InstructionGrammarData> instructionGrammarDatas = new Gson().fromJson(req.body(), List.class);
            //List<InstructionGrammar> instructionGrammars = InstructionGrammarParser.process(instructionGrammarDatas);

            List<String> str = new Gson().fromJson(req.body(), List.class);


            return str;
        }, new JsonTransformer());

        post("/api/protected/assemble", (req, res) -> {
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            res.status(201);

            String[] result = assemble(list);

            String asm = req.body();
            String imem = result[0];
            String dmem = result[1];

            ObjectId id = create(asm, imem, dmem);

            AssemblerResponse ar = new AssemblerResponse();
            ar.setId(id.toString());
            ar.setAsm(asm);
            ar.setImem(imem);
            ar.setDmem(dmem);
            ar.setErrors(result[2]);
            return ar;
        }, new JsonTransformer());

        get("/files/assembler/:id/file.asm", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=\"file.asm\"");
            return find(req.params(":id")).getAsm();
        });

        get("/files/assembler/:id/imem.mif", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=imem.mif");
            return find(req.params(":id")).getImem();
        });

        get("/files/assembler/:id/dmem.mif", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=imem.mif");
            return find(req.params(":id")).getDmem();
        });
    }

    private String[] assemble(List<String> strings) {
        Assembler a = new ECE350Assembler();
        a.parse(strings);
        List<String> imemStrings = a.toImemBinary();
        List<String> dmemStrings = a.toDmemBinary();

        Stringer w = new Stringer();

        String[] returnArr = new String[3];
        returnArr[0] = w.toMif(imemStrings);
        returnArr[1] = w.toMif(dmemStrings);
        returnArr[2] = a.getErrorString();
        return returnArr;
    }

    public ObjectId create(String asm, String imemMif, String dmemMif) {
        //Asm asm = new Gson().fromJson(body, Asm.class);
        BasicDBObject doc = new BasicDBObject("asm", asm).append("imem", imemMif).append("dmem", dmemMif).append("createdOn", new Date());
        assembler.insert(doc);
        ObjectId id = (ObjectId) doc.get("_id");
        return id;
    }

    public Asm find(String id) {
        return new Asm((BasicDBObject) assembler.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
