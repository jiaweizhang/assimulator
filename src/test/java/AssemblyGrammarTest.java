import assemble.parser.AssemblyParser;
import assemble.parser.InstructionGrammar;
import assemble.parser.PseudoInstructionGrammar;
import com.google.gson.Gson;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jiaweizhang on 6/17/2016.
 */
public class AssemblyGrammarTest {

    @Test
    public void Test() throws IOException {
        AssemblyParser assemblyParser = new AssemblyParser();

        String pseudoInstructionGrammarJson = readFile("src/data/ece350pseudoinstructiongrammar.json", Charset.defaultCharset());
        List<PseudoInstructionGrammar> pseudoInstructionGrammars = Arrays.asList(new Gson().fromJson(pseudoInstructionGrammarJson, PseudoInstructionGrammar[].class));
        assemblyParser.setPseudoInstructionGrammars(pseudoInstructionGrammars);

        String instructionGrammarJson = readFile("src/data/ece350instructiongrammar.json", Charset.defaultCharset());
        List<InstructionGrammar> instructionGrammars = Arrays.asList(new Gson().fromJson(instructionGrammarJson, InstructionGrammar[].class));
        assemblyParser.setInstructionGrammars(instructionGrammars);

        List<String> assembly = Files.readAllLines(Paths.get("src/data/ece350.asm"), Charset.defaultCharset());

        assemblyParser.process(assembly);

        assemblyParser.getBinary()
                .stream()
                .forEach(s -> System.out.println(s));

        System.out.println();

        assemblyParser.getErrors()
                .stream()
                .forEach(s -> System.out.println(s));
    }

    static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
