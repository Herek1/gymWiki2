package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.dao.UsersDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AuthHandler implements HttpHandler {
    UsersDAO newUsersDAO;
    List<HashMap<String, String>> userList = new ArrayList<>();
    public AuthHandler(UsersDAO newUsersDAO) {
        this.newUsersDAO = newUsersDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("===== Incoming Request Auth=====");
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
        userList = newUsersDAO.getUser(login, password);

        String response;
        if (!userList.isEmpty() && userList.get(0).containsKey("userType")) {
            String type = userList.get(0).get("userType");
            System.out.println("Authenticated as " + type.toUpperCase());
            response = String.format("{\"status\": \"ok\", \"privilege\": \"%s\"}", type);
        } else {
            System.out.println("Authentication FAILED");
            response = "{\"status\": \"fail\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }

        System.out.println("Response sent: " + response);
        System.out.println("=============================");
    }
}
