package models;

import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class Credentials {
    private String id;
    private String username;
    private String password;

    public Credentials(BasicDBObject dbObject) {
        this.id = ((ObjectId) dbObject.get("_id")).toString();
        this.username = dbObject.getString("username");
        this.password = dbObject.getString("password");
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
