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
    private Date createdOn = new Date();

    public Asm(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.asm = dbObject.getString("asm");
    }

    public String getAsm() {
        return asm;
    }
}
