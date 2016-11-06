import assimulator.assemble.AssemblerRequest;
import assimulator.assemble.parser.InstructionGrammar;
import assimulator.assemble.parser.MacroGrammar;
import com.google.gson.Gson;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jiaweizhang on 6/24/2016.
 */
public class AssemblerEndpointTest {

	@Test
	public void TestEndpointTest() throws IOException {
		HttpClient httpClient = HttpClientBuilder.create().build(); //Use this instead

		String macroGrammarJson = readFile("src/data/ece350.json", Charset.defaultCharset());
		List<MacroGrammar> macroGrammars = Arrays.asList(new Gson().fromJson(macroGrammarJson, MacroGrammar[].class));

		String instructionGrammarJson = readFile("src/data/ece350.json", Charset.defaultCharset());
		List<InstructionGrammar> instructionGrammars = Arrays.asList(new Gson().fromJson(instructionGrammarJson, InstructionGrammar[].class));

		List<String> assembly = Files.readAllLines(Paths.get("src/data/asm/ece350.asm"), Charset.defaultCharset());

		AssemblerRequest assemblerRequest = new AssemblerRequest();
		assemblerRequest.instructions = assembly;
		assemblerRequest.instructionGrammars = instructionGrammars;
		assemblerRequest.macroGrammars = macroGrammars;

		String jsonBody = new Gson().toJson(assemblerRequest);

		System.out.println(jsonBody);

		HttpPost request = new HttpPost("http://localhost:8080/api/v3/assemble");
		StringEntity params = new StringEntity(jsonBody);
		request.addHeader("content-type", "application/json");
		request.setEntity(params);
		HttpResponse response = httpClient.execute(request);
		System.out.println(response.toString());

		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		System.out.println(result);
	}

	static String readFile(String path, Charset encoding)
			throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
