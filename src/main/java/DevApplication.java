import assemble.Assembler;
import assemble.ECE350Assembler;
import io.Reader;
import io.Stringer;
import models.IntLine;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by jiaweizhang on 4/12/16.
 */
public class DevApplication {
    public static void main(String args[]) throws FileNotFoundException {
        Reader r = new Reader();
        List<String> strings = r.readFile("src/format/asm/test.asm");
        Assembler a = new ECE350Assembler();
        a.parse(strings);

        List<String> imemStrings = a.toImemBinary();

        List<String> dmemStrings = a.toDmemBinary();

        Stringer w = new Stringer();

        System.out.println(w.toMif(imemStrings));
        System.out.println(w.toMif(dmemStrings));
    }
}


