package auth;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class AuthResponse {
    private String id_token;
    private String error;

    public String getId_token() {
        return id_token;
    }

    public void setId_token(String id_token) {
        this.id_token = id_token;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
