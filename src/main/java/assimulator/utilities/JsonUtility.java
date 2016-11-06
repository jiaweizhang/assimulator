package assimulator.utilities;

import assimulator.assemble.parser.InstructionGrammar;
import assimulator.assemble.parser.MacroGrammar;
import com.google.gson.Gson;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jiaweizhang on 11/6/2016.
 */
public class JsonUtility {

    private static final Gson gson = new Gson();

    public static List<InstructionGrammar> loadInstructionGrammar(String fileName) {
        Resource resource = new ClassPathResource("instructionGrammar/" + fileName + ".json");
        InputStream resourceInputStream = null;
        try {
            resourceInputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resourceString = convertStreamToString(resourceInputStream);
        return Arrays.asList(gson.fromJson(resourceString, InstructionGrammar[].class));
    }

    public static List<MacroGrammar> loadMacroGrammar(String fileName) {
        Resource resource = new ClassPathResource("macroGrammar/" + fileName + ".json");
        InputStream resourceInputStream = null;
        try {
            resourceInputStream = resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String resourceString = convertStreamToString(resourceInputStream);
        return Arrays.asList(gson.fromJson(resourceString, MacroGrammar[].class));
    }

    // http://stackoverflow.com/questions/309424/read-convert-an-inputstream-to-a-string
    private static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
