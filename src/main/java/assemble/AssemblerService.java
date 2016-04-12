package assemble;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
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
    private final DBCollection assemblerasms;
    private final DBCollection assemblerImemMifs;
    private final DBCollection assemblerDmemMifs;

    public AssemblerService(DB db) {
        this.assemblerasms = db.getCollection("assemblerasms");
        this.assemblerImemMifs = db.getCollection("assemblerimemmifs");
        this.assemblerDmemMifs = db.getCollection("assemblerdmemmifs");
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/api/assemble", (req, res) -> {
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            ObjectId asmId = createAsm(req.body());
            res.status(201);

            String[] result = assemble(list);

            ObjectId imemMifId = createImemMif(result[0]);
            ObjectId dmemMifId = createDmemMif(result[1]);
            AssemblerResponse ar = new AssemblerResponse();
            ar.setAsmId(asmId.toString());
            ar.setImemMifId(imemMifId.toString());
            ar.setImemMif(result[0]);
            ar.setDmemMifId(dmemMifId.toString());
            ar.setDmemMif(result[1]);
            return ar;
        }, new JsonTransformer());

        get("/files/assembler/:id/file.asm", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=\"file.asm\"");
            return findAsm(req.params(":id")).getAsm();
        });

        get("/files/assembler/:id/imem.mif", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=imem.mif");
            return findImemMif(req.params(":id")).getMif();
        });

        get("/files/assembler/:id/dmem.mif", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=imem.mif");
            return findDmemMif(req.params(":id")).getMif();
        });
    }

    private String[] assemble(List<String> strings) {
        Assembler a = new ECE350Assembler();
        a.parse(strings);
        List<String> imemStrings = a.toImemBinary();
        List<String> dmemStrings = a.toDmemBinary();

        Stringer w = new Stringer();

        String[] returnArr = new String[2];
        returnArr[0] = w.toMif(imemStrings);
        returnArr[1] = w.toMif(dmemStrings);
        return returnArr;
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

    public ObjectId createImemMif(String body) {
        BasicDBObject doc = new BasicDBObject("mif", body).append("createdOn", new Date());
        assemblerImemMifs.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Mif findImemMif(String id) {
        return new Mif((BasicDBObject) assemblerImemMifs.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }

    public ObjectId createDmemMif(String body) {
        BasicDBObject doc = new BasicDBObject("mif", body).append("createdOn", new Date());
        assemblerDmemMifs.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Mif findDmemMif(String id) {
        return new Mif((BasicDBObject) assemblerDmemMifs.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
