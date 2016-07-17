package assemble;

import assemble.parser.InstructionGrammar;
import assemble.parser.MacroGrammar;

import java.util.List;

/**
 * Created by Jiawei on 6/24/2016.
 */
public class AssemblerRequest {
	public List<InstructionGrammar> instructionGrammars;
	public List<MacroGrammar> macroGrammars;
	public List<String> instructions;
}
