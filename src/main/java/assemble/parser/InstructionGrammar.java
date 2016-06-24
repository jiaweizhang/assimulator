package assemble.parser;

import java.util.List;

/**
 * Created by jiaweizhang on 6/18/2016.
 */
public class InstructionGrammar {
	public String name;
	public String regex;
	public List<SingleVar> vars;
	public List<OpCode> opCodes;
}
