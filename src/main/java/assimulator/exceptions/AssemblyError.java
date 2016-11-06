package assimulator.exceptions;

/**
 * Created by jiaweizhang on 11/6/2016.
 */
public class AssemblyError extends RuntimeException {
	public String message;

	public AssemblyError(String message) {
		this.message = message;
	}
}
