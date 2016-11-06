package assimulator;

/**
 * Created by jiaweizhang on 11/6/2016.
 */

import assimulator.exceptions.AssemblyError;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

import static assimulator.utilities.ResponseUtility.wrap;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleIllegalArgumentException(Exception e) {
		return wrap(new StdResponse(200, false, "Invalid request: " + e.getLocalizedMessage()));
	}

	@ExceptionHandler(AssemblyError.class)
	public ResponseEntity handleAssemblyError(AssemblyError e) {
		return wrap(new StdResponse(200, false, "Assembly error: " + e.message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity handleException(Exception e) {
		return wrap(new StdResponse(500, false, Arrays.toString(e.getStackTrace())));
	}
}
