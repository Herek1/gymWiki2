package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.dao.UsersDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserHandler implements HttpHandler {
    UsersDAO newUsersDAO;
    List<HashMap<String, String>> userList = new ArrayList<>();
    public UserHandler(UsersDAO newUsersDAO) {
        this.newUsersDAO = newUsersDAO;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        System.out.println("===== Incoming Request Users=====");
        System.out.println("Method: " + method);

        switch (method) {
            case "DELETE":
                handleDelete(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
        System.out.println("===== Response Sent =====");
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes());
        System.out.println("Raw body: " + body);

        Boolean deleted = true;
        String response;
        if (deleted) {
            System.out.println("User deleted successfully");
            response = "{\"status\": \"ok\"}";
        } else {
            System.out.println("User deletion failed");
            response = "{\"status\": \"fail\"}";
        }

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (var os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Raw body: " + body);


        String name = "test";
        String response;
        if (!name.isEmpty()) {
            System.out.println("Adding exercise: " + name);
            // add to db here
            response = "{\"status\": \"ok\"}";
            exchange.sendResponseHeaders(200, response.getBytes().length);
        } else {
            response = "{\"status\": \"fail\", \"reason\":\"Missing fields\"}";
            exchange.sendResponseHeaders(400, response.getBytes().length);
        }
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
        System.out.println("Response sent: " + response);
    }
}
