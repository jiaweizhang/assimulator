package assimulator;

import assimulator.assemble.AssemblerRequest;
import assimulator.assemble.AssemblerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static assimulator.utilities.ResponseUtility.wrap;

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
}
