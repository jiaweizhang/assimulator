package assemble;

import com.mongodb.*;
import db.JsonTransformer;
import io.Stringer;
import models.Asm;
import models.IntLine;
import models.Mif;
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
    private final DB db;
    private final DBCollection assemblerasms;
    private final DBCollection assemblermifs;

    public AssemblerService(DB db) {
        this.db = db;
        this.assemblerasms = db.getCollection("assemblerasms");
        this.assemblermifs = db.getCollection("assemblermifs");
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/api/assemble", (req, res) -> {
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            ObjectId asmId = createAsm(req.body());
            res.status(201);

            String result = assemble(list);

            ObjectId mifId = createMif(result);
            AssemblerResponse ar = new AssemblerResponse();
            ar.setAsmId(asmId.toString());
            ar.setMifId(mifId.toString());
            ar.setMif(result);
            return ar;
        }, new JsonTransformer());

        get("/files/assembler/asm/:id/file.asm", (req, res) ->
                findAsm(req.params(":id")).getAsm().replaceAll("(\r\n|\n)", "<br />")
        );

        get("/files/assembler/mif/:id/imem.mif", (req, res) ->
                findMif(req.params(":id")).getMif().replaceAll("(\r\n|\n)", "<br />")
        );
    }

    private String assemble(List<String> strings) {
        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(strings);
        List<String> readableStrings = a.toString(ints);
        List<String> binaryStrings = a.toBinary(ints);

        Stringer w = new Stringer();
        return w.toMif(binaryStrings);
    }

    public List<Asm> findAllAsms() {
        List<Asm> asms = new ArrayList<Asm>();
        DBCursor dbObjects = assemblerasms.find();
        while (dbObjects.hasNext()) {
            DBObject dbObject = dbObjects.next();
            asms.add(new Asm((BasicDBObject) dbObject));
        }
        return asms;
    }

    public ObjectId createAsm(String body) {
        //Asm asm = new Gson().fromJson(body, Asm.class);
        BasicDBObject doc = new BasicDBObject("asm", body).append("createdOn", new Date());
        assemblerasms.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Asm findAsm(String id) {
        return new Asm((BasicDBObject) assemblerasms.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }

    public ObjectId createMif(String body) {
        //Asm asm = new Gson().fromJson(body, Asm.class);
        BasicDBObject doc = new BasicDBObject("mif", body).append("createdOn", new Date());
        assemblermifs.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Mif findMif(String id) {
        return new Mif((BasicDBObject) assemblermifs.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
