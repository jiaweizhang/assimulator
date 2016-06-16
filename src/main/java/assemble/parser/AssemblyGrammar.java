package assemble.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class AssemblyGrammar {
    public Map<String, InstructionGrammar> instructionGrammars;
    public List<String> errors;

    public AssemblyGrammar() {
        errors = new ArrayList<String>();
    }

    public void setInstructionGrammars(Map<String, InstructionGrammar> instructionGrammars) {
        this.instructionGrammars = instructionGrammars;
    }

    public List<String> process(List<String> assembly) {
        if (assembly.size() == 0) {
            errors.add("error: no assembly found");
            return new ArrayList<String>();
        }

        List<StringWithLineNumber> assemblyWithLines = IntStream
                .range(0, assembly.size())
                .mapToObj(i -> new StringWithLineNumber(i, assembly.get(i)))
                .collect(Collectors.toList());

        List<StringWithLineNumber> assemblyWithNoComments = assemblyWithLines
                .stream()
                .map(StringWithLineNumber::removeComments)
                .filter(s -> s.string.length() > 0)
                .collect(Collectors.toList());

        // error if no text
        StringWithLineNumber textHeaderLine = assemblyWithNoComments
                .stream()
                .filter(s -> s.string.substring(0, 5).equals(".text"))
                .findFirst()
                .get();

        // error if no data
        StringWithLineNumber dataHeaderLine = assemblyWithNoComments
                .stream()
                .filter(s -> s.string.substring(0, 5).equals(".data"))
                .findFirst()
                .get();

        Map<String, DataParser.DataContent> vars = new HashMap<String, DataParser.DataContent>();
        DataParser dataParser = new DataParser();

        assemblyWithNoComments
                .stream()
                .filter(s -> {
                    if (dataHeaderLine.line > textHeaderLine.line) {
                        // data is after text
                        return s.line >= dataHeaderLine.line;
                    } else {
                        // text is before data
                        return s.line < textHeaderLine.line && s.line >= dataHeaderLine.line;
                    }
                })
                .map(StringWithLineNumber::removeHeader)
                .filter(s -> s.string.length() > 0)
                .forEach(s -> {
                    if (dataParser.process(vars, s) == null) {
                        errors.add(s.line + "::error: cannot parse data");
                    }
                });

        List<String> binaryInstructions = new ArrayList<String>();
        assemblyWithNoComments
                .stream()
                .filter(s -> {
                    if (textHeaderLine.line > dataHeaderLine.line) {
                        // text is after data
                        return s.line >= textHeaderLine.line;
                    } else {
                        // text is before data
                        return s.line < dataHeaderLine.line && s.line >= textHeaderLine.line;
                    }
                })
                .map(StringWithLineNumber::removeHeader)
                .filter(s -> s.string.length() > 0)
                .forEach(s -> {
                    String[] stringArr = s.string.split("\\s+");
                    if (instructionGrammars.containsKey(stringArr[0])) {
                        String result = instructionGrammars.get(stringArr[0]).process(s.string);
                        List<String> errors = instructionGrammars.get(stringArr[0]).errors;
                        errors = Stream.of(errors)
                                .map(e -> Integer.toString(s.line) + "::" + e)
                                .collect(Collectors.toList());
                        this.errors.addAll(errors);
                        binaryInstructions.add(result);
                    } else {
                        this.errors.add(s.line + "::Unrecognized instruction");
                    }
                });

        return binaryInstructions;
    }
}
