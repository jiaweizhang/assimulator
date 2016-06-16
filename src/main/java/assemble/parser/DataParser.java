package assemble.parser;

import java.util.Map;

/**
 * Created by jiaweizhang on 6/15/2016.
 */
public class DataParser {
    public Map<String, DataContent> process(Map<String, DataContent> map, StringWithLineNumber string) {
        String[] stringArr = string.string.split("\\s+");
        if (stringArr.length != 3) {
            return null;
        }

        DataContent dataContent = new DataContent();
        if (stringArr[1].equals(".int")) {
            dataContent.dataTypeEnum = DataTypeEnum.INT;
            dataContent.integer = Integer.parseInt(stringArr[2]);
        } else if (stringArr[1].equals(".word")) {
            dataContent.dataTypeEnum = DataTypeEnum.WORD;
            dataContent.word = Integer.decode(stringArr[2]);
        } else if (stringArr[1].equals(".char")) {
            dataContent.dataTypeEnum = DataTypeEnum.WORD;
            dataContent.character = stringArr[2].charAt(0);
        } else if (stringArr[1].equals(".string")) {
            dataContent.dataTypeEnum = DataTypeEnum.WORD;
            dataContent.string = stringArr[2];
        } else {
            // return null if illegal
            return null;
        }

        map.put(stringArr[0].substring(0, stringArr[0].length() - 1), dataContent);
        return map;
    }

    public class DataContent {
        public DataTypeEnum dataTypeEnum;
        public int integer;
        public int word;
        public char character;
        public String string;
    }

    public enum DataTypeEnum {
        INT,
        WORD,
        CHAR,
        STRING
    }
}
