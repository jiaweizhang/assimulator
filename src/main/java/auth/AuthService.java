package auth;

import com.google.gson.Gson;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import db.JsonTransformer;
import models.Credentials;

import static spark.Spark.post;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class AuthService {
    private final DBCollection auth;

    public AuthService(DB db) {
        this.auth = db.getCollection("auth");
        setupEndpoints();
    }

    private void setupEndpoints() {
        post("/api/signup", (req, res) -> {
            Credentials cred = new Gson().fromJson(req.body(), Credentials.class);
            System.out.println(cred.getUsername() + ": username");
            AuthResponse ar = new AuthResponse();
            ar.setId_token("valid_token");
            if (cred.getUsername().equals("username")) {
                return ar;
            } else {
                return "";
            }
        }, new JsonTransformer());

        post("/api/login", (req, res) -> {
            Credentials cred = new Gson().fromJson(req.body(), Credentials.class);
            System.out.println(cred.getUsername() + ": username");
            AuthResponse ar = new AuthResponse();
            ar.setId_token("valid_token");
            if (cred.getUsername().equals("username")) {
                return ar;
            } else {
                return "";
            }
        }, new JsonTransformer());
    }
}
