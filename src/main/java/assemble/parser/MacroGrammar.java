package assemble.parser;

import java.util.List;

/**
 * Created by jiaweizhang on 6/17/2016.
 */
public class MacroGrammar {
	// using regex matcher
	// example:
	// bgt -> blt
	// regex = "\\s*\\$(\\d+)\\s*,\\s*\\$(\\d+)\\s*,\\s*([a-zA-Z0-9]+)\\s*";
	// note that regex has 3 arguments
	// result[0] = bgt $(2), $(1), (3)

	public String name;
	public String regex;
	public List<String> results;
}
