package disassemble.numberparse;

/**
 * Created by jiaweizhang on 4/8/16.
 */
public class DecParser implements NumberParser{
    @Override
    public int parse(String in) {
        return Integer.parseInt(in);
    }
}
