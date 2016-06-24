import assemble.AssemblerService;

import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.port;


/**
 * Created by jiaweizhang on 4/7/16.
 */
public class Application {

	private static final boolean production = false;

	public static void main(String args[]) {
		if (production) {
			port(80);
		} else {
			port(8080);
		}

		System.out.println(System.getProperty("user.dir"));
		String staticFileLocation = System.getProperty("user.dir") + "/webapp";
		externalStaticFileLocation(staticFileLocation);

		new AssemblerService();
	}
}
