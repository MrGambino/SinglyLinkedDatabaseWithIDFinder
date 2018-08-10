import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class LDB_Server {
	public static void main(String[] args) throws Exception {
		System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() +" - Server Starting ....");
		HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8181), 8181);
		System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() +" - DEFAULT URL: http://localhost:8181/auth/login/index.html\n");
		server.createContext("/auth/login", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			String response = "Welcome to LinkedDatabase Framework!";			
			
			File file = new File("LDB Framework/interface/index.html");
			t.sendResponseHeaders(200, file.length());
			try (OutputStream os = t.getResponseBody()) {
			    Files.copy(file.toPath(), os);
			}
			
			System.out.println(LDB_SaveAndPopulateUniqueIDs.updateCurrentTime() +" - REQUESTED: "+ t.getRequestURI());
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			t.getResponseHeaders().set("Content-Type", "text/html"); 
			os.write(response.getBytes());
			os.close();
		}
	}
}