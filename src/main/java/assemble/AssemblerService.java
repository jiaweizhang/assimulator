package assemble;

import assemble.parser.AssemblyParser;
import com.google.gson.Gson;
import json.JsonTransformer;

import java.util.ArrayList;
import java.util.List;

import static spark.Spark.post;

/**
 * Created by jiaweizhang on 4/11/16.
 */
public class AssemblerService {

	public AssemblerService() {
		setupEndpoints();
	}

	private void setupEndpoints() {
		post("/api/v3/assemble", (req, res) -> {
			System.out.println(req.body());

			AssemblerRequest assemblerRequest = new Gson().fromJson(req.body(), AssemblerRequest.class);

			AssemblyParser assemblyParser = new AssemblyParser();
			assemblyParser.setInstructionGrammars(assemblerRequest.instructionGrammars);
			assemblyParser.setMacroGrammars(assemblerRequest.macroGrammars);
			boolean success = assemblyParser.process(assemblerRequest.instructions);

			AssemblerResponse assemblerResponse = new AssemblerResponse();
			if (success) {
				List<String> output = assemblyParser.getBinary();
				assemblerResponse.binary = InstructionPackager.pack(output);
				assemblerResponse.errors = new ArrayList<>();
			} else {
				List<String> errors = assemblyParser.getErrors();
				assemblerResponse.errors = errors;
				assemblerResponse.binary = "";
			}
			return assemblerResponse;
		}, new JsonTransformer());
	}
}
