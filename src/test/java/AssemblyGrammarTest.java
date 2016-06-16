import assemble.parser.AssemblyGrammar;
import assemble.parser.InstructionGrammar;
import assemble.parser.OpCode;
import assemble.parser.SingleVar;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class AssemblyGrammarTest {
    private final String RRR = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*";
    private final String RRI = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*(\\d+)\\s*";
    private final String RIR = "\\s*\\$(\\d+)\\s*,\\s*(\\d+)\\s*\\(\\s*\\$(\\d+)\\s*\\)\\s*";

    @Test
    public void SimpleParseTest() {
        AssemblyGrammar assemblyGrammar = new AssemblyGrammar();
        assemblyGrammar.instructionGrammars = generateInstructionGrammars();
        List<String> binaryOutput = assemblyGrammar.process(Arrays.asList(
            ".text",
            "add $1, $2, $3",
            "sub $2, $3, $4",
            ".data",
            "var: .int 15",
            "var2: .word 0x20239829"
        ));
    }

    private Map<String, InstructionGrammar> generateInstructionGrammars() {
        InstructionGrammar instructionGrammar1 = new InstructionGrammar();
        instructionGrammar1.name = "add";
        instructionGrammar1.regex = RRR;
        instructionGrammar1.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(5, 17),
                new SingleVar(5, 12)
        );
        instructionGrammar1.opCodes = Arrays.asList(
                new OpCode(0, 2)
        );

        InstructionGrammar instructionGrammar2 = new InstructionGrammar();
        instructionGrammar2.name = "sub";
        instructionGrammar2.regex = RRR;
        instructionGrammar2.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(5, 17),
                new SingleVar(5, 12)
        );
        instructionGrammar2.opCodes = Arrays.asList(
                new OpCode(1, 2)
        );

        List<InstructionGrammar> grammarList = Arrays.asList(
                instructionGrammar1,
                instructionGrammar2
        );

        return grammarList
                .stream()
                .collect(Collectors.toMap(c -> c.name, c -> c));
    }
}
