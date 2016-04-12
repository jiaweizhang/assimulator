package models;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

import java.util.Date;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class Mif {
    private String id;
    private String mif;
    private Date createdOn = new Date();

    public Mif(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.mif = dbObject.getString("mif");
    }

    public String getMif() {
        return mif;
    }
}
