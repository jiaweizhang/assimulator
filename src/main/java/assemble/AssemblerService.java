package assemble;

import db.JsonTransformer;
import io.Stringer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static spark.Spark.post;

/**
 * Created by jiaweizhang on 4/11/16.
 */
public class AssemblerService {
    public AssemblerService() {
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/api/protected/assemble", (req, res) -> {
            String[] arr = req.body().split("\n");
            List<String> list = new ArrayList<String>(Arrays.asList(arr));

            res.status(201);

            String[] result = assemble(list);

            String asm = req.body();
            String imem = result[0];
            String dmem = result[1];

            AssemblerResponse ar = new AssemblerResponse();
            ar.setId("some random ID");
            ar.setAsm(asm);
            ar.setImem(imem);
            ar.setDmem(dmem);
            ar.setErrors(result[2]);
            return ar;
        }, new JsonTransformer());
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
}
