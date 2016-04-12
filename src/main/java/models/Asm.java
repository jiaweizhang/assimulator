package models;


import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by jiaweizhang on 4/11/16.
 */
public class Asm {
    private String id;
    private String asm;
    private String imem;
    private String dmem;
    private Date createdOn = new Date();

    public Asm(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.asm = dbObject.getString("asm");
        this.imem = dbObject.getString("imem");
        this.dmem = dbObject.getString("dmem");
    }

    public String getAsm() {
        return asm;
    }

    public String getImem() {
        return imem;
    }

    public String getDmem() {
        return dmem;
    }
}
