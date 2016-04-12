package auth;

import com.google.gson.Gson;
import com.mongodb.*;
import db.JsonTransformer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import models.Credentials;
import spark.Request;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

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
            ar.setId_token(createJwt(cred.getUsername()));
            if (createUser(cred.getUsername(), cred.getPassword())) {
                return ar;
            } else {
                return "";
            }
        }, new JsonTransformer());

        post("/api/login", (req, res) -> {
            Credentials cred = new Gson().fromJson(req.body(), Credentials.class);
            System.out.println(cred.getUsername() + " >> username");
            AuthResponse ar = new AuthResponse();
            ar.setId_token(createJwt(cred.getUsername()));
            if (loginUser(cred.getUsername(), cred.getPassword())) {
                return ar;
            } else {
                return "";
            }
        }, new JsonTransformer());
    }

    private boolean createUser(String username, String password) {
        String hash = null;
        try {
            hash = PasswordStorage.createHash(password);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            return false;
        }

        DBObject query = new BasicDBObject("username", username);
        DBCursor result = auth.find(query);
        if (result.size() >= 1) {
            return false;
        }

        BasicDBObject doc = new BasicDBObject("username", username).append("hash", hash).append("createdOn", new Date());
        auth.insert(doc);
        return true;
    }

    private boolean loginUser(String username, String password) {
        DBObject query = new BasicDBObject("username", username);
        DBCursor result = auth.find(query);
        String hash = null;
        while (result.hasNext()) {
            hash = result.next().get("hash").toString();
            System.out.println(hash);
        }
        try {
            return PasswordStorage.verifyPassword(password, hash);
        } catch (PasswordStorage.CannotPerformOperationException e) {
            return false;
        } catch (PasswordStorage.InvalidHashException e) {
            return false;
        }
    }

    private String createJwt(String username) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        // TODO set real secret_key
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("secret_key");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder().setId(username)
                .setIssuedAt(now)
                .setSubject(username)
                .signWith(signatureAlgorithm, signingKey);

        return builder.compact();
    }

    public boolean verify(Request req) {
        String header = req.headers("Authorization");
        System.out.println(header);
        return parseJWT(header.substring(7));
    }

    private boolean parseJWT(String jwt) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("secret_key"))
                    .parseClaimsJws(jwt).getBody();
            System.out.println("ID: " + claims.getId());
            System.out.println("Subject: " + claims.getSubject());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
