package assemble.parser;

import java.util.List;

/**
 * Created by jiaweizhang on 6/16/2016.
 */
public class InstructionGrammarData {
    public String name;
    public String regex;
    public List<SingleVar> vars;
    public List<OpCode> opCodes;
}
