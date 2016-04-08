import assemble.Assembler;
import assemble.ECE350Assembler;
import assemble.Sanitizer;
import io.Reader;
import io.Writer;
import models.IntLine;
import models.StringLine;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {
    public static void main(String args[]) {
        // System.out.println(System.getProperty("user.dir"));
        Application a = new Application();
        a.assemble();
    }

    private void assemble() {
        Reader r = new Reader();
        List<String> strings = null;
        try {
            strings = r.readFile("asm/test.asm");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        }

        Sanitizer s = new Sanitizer();
        List<StringLine> sl = s.asmSanitize(strings);

        Assembler a = new ECE350Assembler();
        List<IntLine> ints = a.parse(sl);
        List<String> readableStrings = a.toString(ints);
        List<String> binaryStrings = a.toBinary(ints);

        Writer w = new Writer();
        w.toConsole(readableStrings);
        w.toMifConsole(binaryStrings);
    }
}
