package assimulator;

import java.sql.Timestamp;

/**
 * Created by jiaweizhang on 11/6/2016.
 */
public class StdResponse {
	public int status;
	public boolean success;
	public String message;
	public Timestamp timestamp;
	public Object body;

	public StdResponse(int status, boolean success, String message, Object body) {
		this.status = status;
		this.success = success;
		this.message = message;
		this.body = body;
	}

	public StdResponse(int status, boolean success, String message) {
		this(status, success, message, null);
	}
}
