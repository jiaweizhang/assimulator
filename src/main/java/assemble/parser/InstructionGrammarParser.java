package assemble.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by jiaweizhang on 6/16/2016.
 */
public class InstructionGrammarParser {
    public static List<InstructionGrammar> process(List<InstructionGrammarData> dataList) {
        List<InstructionGrammar> out = new ArrayList<InstructionGrammar>();
        dataList.stream().forEach(g -> {
            InstructionGrammar instructionGrammar = new InstructionGrammar();
            instructionGrammar.name = g.name;
            instructionGrammar.regex = g.regex;
            instructionGrammar.vars = g.vars;
            instructionGrammar.opCodes = g.opCodes;
        });
        return out;
    }
}
