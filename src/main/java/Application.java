import assemble.AssemblerService;
import spark.Filter;
import spark.Request;
import spark.Response;

import static spark.Spark.before;
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
		String staticFileLocation = System.getProperty("user.dir") + "/src/static";
		externalStaticFileLocation(staticFileLocation);

		enableCORS("*", "*", "*");

		new AssemblerService();
	}

	private static void enableCORS(final String origin, final String methods, final String headers) {
		before(new Filter() {
			@Override
			public void handle(Request request, Response response) {
				response.header("Access-Control-Allow-Origin", origin);
				response.header("Access-Control-Request-Method", methods);
				response.header("Access-Control-Allow-Headers", headers);
			}
		});
	}
}
