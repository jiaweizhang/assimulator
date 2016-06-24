package assemble;

import java.util.List;

/**
 * Created by jiaweizhang on 6/24/2016.
 */
public class InstructionPackager {
	public static String pack(List<String> instructions) {
		StringBuilder sb = new StringBuilder();
		sb.append("DEPTH = 4096;\n");
		sb.append("WIDTH = 32;\n");
		sb.append("ADDRESS_RADIX = DEC;\n");
		sb.append("DATA_RADIX = BIN;\n");
		sb.append("CONTENT\n");
		sb.append("BEGIN\n\n");
		for (int i = 0; i < instructions.size(); i++) {
			sb.append(i + " : " + instructions.get(i) + ";\n");
		}
		sb.append("\nEND;");
		return sb.toString();
	}
}
