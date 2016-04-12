package disassemble;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import db.JsonTransformer;
import io.Stringer;
import models.Asm;
import models.Mif;
import models.StringLine;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static spark.Spark.get;
import static spark.Spark.post;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class DisassemblerService {
    private final DB db;
    private final DBCollection disassembleasms;
    private final DBCollection disassemblemifs;

    public DisassemblerService(DB db) {
        this.db = db;
        this.disassembleasms = db.getCollection("disassemblerasms");
        this.disassemblemifs = db.getCollection("disassemblermifs");
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/api/disassemble", (req, res) -> {
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            ObjectId mifId = createMif(req.body());
            res.status(201);

            String result = disassemble(list);

            ObjectId asmId = createAsm(result);
            DisassemblerResponse ar = new DisassemblerResponse();
            ar.setAsmId(asmId.toString());
            ar.setMifId(mifId.toString());
            ar.setAsm(result);
            return ar;
        }, new JsonTransformer());

        get("/files/disassembler/asm/:id/file.asm", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=\"file.asm\"");
            return findAsm(req.params(":id")).getAsm();
        });

        get("/files/disassembler/mif/:id/imem.mif", (req, res) -> {
            //res.header("Content-Disposition", "attachment; filename=imem.mif");
            return findMif(req.params(":id")).getMif();
        });
    }

    private String disassemble(List<String> strings) {
        Disassembler d = new ECE350Disassembler();
        List<StringLine> parsed = d.parse(strings);
        List<String> readable = d.toString(parsed);

        Stringer w = new Stringer();
        return w.toAsm(readable);
    }

    public ObjectId createAsm(String body) {
        BasicDBObject doc = new BasicDBObject("asm", body).append("createdOn", new Date());
        disassembleasms.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Asm findAsm(String id) {
        return new Asm((BasicDBObject) disassembleasms.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }

    public ObjectId createMif(String body) {
        BasicDBObject doc = new BasicDBObject("mif", body).append("createdOn", new Date());
        disassemblemifs.insert(doc);
        ObjectId id = (ObjectId)doc.get( "_id" );
        return id;
    }

    public Mif findMif(String id) {
        return new Mif((BasicDBObject) disassemblemifs.findOne(new BasicDBObject("_id", new ObjectId(id))));
    }
}
