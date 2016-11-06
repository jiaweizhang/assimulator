package assimulator;

/**
 * Created by jiaweizhang on 11/6/2016.
 */

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler extends Controller {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleIllegalArgumentException(Exception e) {
        return wrap(new StdResponse(200, false, "Invalid request: " + e.getLocalizedMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return wrap(new StdResponse(500, false, e.getLocalizedMessage()));
    }
}
