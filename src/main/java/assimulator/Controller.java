package assimulator;

import assimulator.assemble.AssemblerRequest;
import assimulator.assemble.AssemblerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by jiaweizhang on 11/6/2016.
 */

@RestController
public class Controller {

    @Autowired
    private AssemblerService assemblerService;

    @RequestMapping(value = "/api/v3/assemble",
            method = RequestMethod.POST,
            headers = {"Content-type=application/json"})
    @ResponseBody
    public ResponseEntity assemble(@RequestBody final AssemblerRequest assemblerRequest) {
        return wrap(assemblerService.assemble(assemblerRequest));
    }

    public ResponseEntity wrap(StdResponse stdResponse) {
        stdResponse.timestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        switch (stdResponse.status) {
            case 200:
                return ResponseEntity.status(HttpStatus.OK).body(stdResponse);
            case 403:
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(stdResponse);
            case 500:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(stdResponse);
            default:
                return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).body(stdResponse);
        }
    }
}
