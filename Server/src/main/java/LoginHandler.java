import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class LoginHandler {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
        server.createContext("/auth", new AuthHandler());
        server.setExecutor(null);
        System.out.println("Login server running on http://localhost:8090/auth");
        server.start();
    }

    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("===== Incoming Request =====");
            System.out.println("Method: " + exchange.getRequestMethod());

            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                System.out.println("Rejected: Invalid method");
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
                return;
            }

            String body = new String(exchange.getRequestBody().readAllBytes());
            System.out.println("Raw body: " + body);

            String[] parts = body.split("&");
            String login = "";
            String password = "";

            for (String part : parts) {
                String[] kv = part.split("=");
                if (kv.length == 2) {
                    if (kv[0].equals("login")) login = kv[1];
                    if (kv[0].equals("password")) password = kv[1];
                }
            }

            System.out.println("Parsed login: " + login);
            System.out.println("Parsed password: " + password);

            String response;
            if (login.equals("user") && password.equals("user")) {
                response = "{\"status\": \"ok\", \"privilege\": 1}";
                System.out.println("Authenticated as USER");
            } else if (login.equals("admin") && password.equals("admin")) {
                response = "{\"status\": \"ok\", \"privilege\": 2}";
                System.out.println("Authenticated as ADMIN");
            } else {
                response = "{\"status\": \"fail\"}";
                System.out.println("Authentication FAILED");
            }

            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

            System.out.println("Response sent: " + response);
            System.out.println("=============================");
        }
    }
}
