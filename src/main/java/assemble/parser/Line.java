package assemble.parser;

/**
 * Created by jiaweizhang on 6/17/2016.
 */
public class Line {
	// line number
	public int line;

	// original string
	public String text;

	// instruction line number
	// used for branching and jumping
	public int instructionLine;
}
