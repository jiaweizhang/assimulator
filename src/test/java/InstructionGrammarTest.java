import assemble.parser.InstructionGrammar;
import assemble.parser.OpCode;
import assemble.parser.SingleVar;
import org.junit.Test;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by jiaweizhang on 6/15/2016.
 */

public class InstructionGrammarTest {
    private final String RRR = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*";
    private final String RRI = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*(\\d+)\\s*";
    private final String RIR = "\\s*\\$(\\d+)\\s*,\\s*(\\d+)\\s*\\(\\s*\\$(\\d+)\\s*\\)\\s*";

    @Test
    public void test() {

    }

    @Test
    public void singleInstructionTest() {
        String insn = "$10, $12, $13";
        String regex = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*";

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(insn);

        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            System.out.println("Found value: " + m.group(2));
            System.out.println("Found value: " + m.group(3));
        } else {
            System.out.println("NO MATCH");
        }
    }

    @Test
    public void addTest() {
        InstructionGrammar instructionGrammar = new InstructionGrammar();
        instructionGrammar.name = "add";
        instructionGrammar.regex = RRR;
        instructionGrammar.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(5, 17),
                new SingleVar(5, 12)
        );
        instructionGrammar.opCodes = Arrays.asList(
                new OpCode(0, 27),
                new OpCode(0, 2)
        );

        String binary = instructionGrammar.process("add $27, $14, $5");
        assertEquals("00000011010111000101000000000000", binary);
    }

    @Test
    public void subTest() {
        InstructionGrammar instructionGrammar = new InstructionGrammar();
        instructionGrammar.name = "sub";
        instructionGrammar.regex = RRR;
        instructionGrammar.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(5, 17),
                new SingleVar(5, 12)
        );
        instructionGrammar.opCodes = Arrays.asList(
                new OpCode(0, 27),
                new OpCode(1, 2)
        );

        String binary = instructionGrammar.process("sub $13, $14, $5");
        assertEquals("00000011010111000101000000000100", binary);
    }

    @Test
    public void sllTest() {
        InstructionGrammar instructionGrammar = new InstructionGrammar();
        instructionGrammar.name = "sll";
        instructionGrammar.regex = RRI;
        instructionGrammar.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(5, 17),
                new SingleVar(5, 7)
        );
        instructionGrammar.opCodes = Arrays.asList(
                new OpCode(0, 27),
                new OpCode(4, 2)
        );

        String binary = instructionGrammar.process("sll $13, $14, 5");
        assertEquals("00000011010111000000001010010000", binary);
    }

    @Test
    public void lwTest() {
        InstructionGrammar instructionGrammar = new InstructionGrammar();
        instructionGrammar.name = "lw";
        instructionGrammar.regex = RIR;
        instructionGrammar.vars = Arrays.asList(
                new SingleVar(5, 22),
                new SingleVar(17, 0),
                new SingleVar(5, 17)
        );
        instructionGrammar.opCodes = Arrays.asList(
                new OpCode(8, 27)
        );

        String binary = instructionGrammar.process("lw $13, 127($14)");
        assertEquals("01000011010111000000000001111111", binary);
    }
}