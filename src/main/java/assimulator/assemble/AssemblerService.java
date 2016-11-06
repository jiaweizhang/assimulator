package assimulator.assemble;

import assimulator.utilities.JsonUtility;
import assimulator.StdResponse;
import assimulator.assemble.parser.AssemblyParser;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiaweizhang on 11/6/16.
 */

@Service
public class AssemblerService {

    public StdResponse assemble(AssemblerRequest assemblerRequest) {
        AssemblyParser assemblyParser = new AssemblyParser();
        if (assemblerRequest.instructionGrammars == null || assemblerRequest.instructionGrammars.size() == 0) {
            // load from resources
            assemblyParser.setInstructionGrammars(JsonUtility.loadInstructionGrammar("ece350"));
        } else {
            assemblyParser.setInstructionGrammars(assemblerRequest.instructionGrammars);
        }
        if (assemblerRequest.macroGrammars == null || assemblerRequest.macroGrammars.size() == 0) {
            // load from resources
            assemblyParser.setMacroGrammars(JsonUtility.loadMacroGrammar("ece350"));
        } else {
            assemblyParser.setMacroGrammars(assemblerRequest.macroGrammars);
        }
        boolean success = assemblyParser.process(assemblerRequest.instructions);

        AssemblerResponse assemblerResponse = new AssemblerResponse();
        if (success) {
            List<String> output = assemblyParser.getBinary();
            assemblerResponse.binary = InstructionPackager.pack(output);
            assemblerResponse.errors = new ArrayList<>();
        } else {
            assemblerResponse.errors = assemblyParser.getErrors();
            assemblerResponse.binary = "";
        }
        return new StdResponse(200, true, "Returning assemble output", assemblerResponse);
    }
}
