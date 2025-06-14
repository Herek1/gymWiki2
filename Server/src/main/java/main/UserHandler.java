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
            case "PUT":
                handlePut(exchange);
            break;
            case "GET":
                handleGet(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
        }
        System.out.println("===== Response Sent =====");
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        String login = "";

        if (query != null) {
            for (String param : query.split("&")) {
                String[] kv = param.split("=");
                if (kv.length == 2 && kv[0].equals("login")) {
                    login = kv[1];
                }
            }
        }

        System.out.println("Delete request for login: " + login);
        List<HashMap<String, String>> result = newUsersDAO.deleteUser(login);
        System.out.println("Delete result: " + result);
        boolean deleted = result.size() > 1 && "Success".equalsIgnoreCase(result.get(1).get("status"));

        String response = deleted
                ? "{\"status\": \"ok\"}"
                : "{\"status\": \"fail\"}";

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }


    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Raw body: " + body);

        String[] parts = body.split("&");
        String login = "";
        String password = "";
        String userType = "";

        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("login")) login = kv[1];
                if (kv[0].equals("password")) password = kv[1];
                if (kv[0].equals("userType")) userType = kv[1];
            }
        }
        String response;
        if (!login.isEmpty() && !password.isEmpty() && !userType.isEmpty()) {
            newUsersDAO.createUser(login, password, userType, "", "");
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

    private void handlePut(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Raw body: " + body);

        String[] parts = body.split("&");
        String requestType = "";
        String login = "";
        String password = "";
        String role = "";
        List<HashMap<String, String>> result = new ArrayList<>();

        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("req")) requestType = kv[1];
                if (kv[0].equals("login")) login = kv[1];
                if (kv[0].equals("password")) password = kv[1];
                if (kv[0].equals("userType")) role = kv[1];
            }
        }
        System.out.println("Parsed request type: " + requestType);
        System.out.println("Parsed login: " + login);
        System.out.println("Parsed new password: " + password);
        switch (requestType){
            case "edit":
                result = newUsersDAO.updateUserPassword(login, password);
                break;
            case "register":
                System.out.println("Registering user: " + login);
                result = newUsersDAO.createUser(login, password, "user", "", "");
                break;
            case "editRole":
                System.out.println("Editing user role for: " + login);
                result = newUsersDAO.updateUserRole(login, role);
                break;
            default:
                result = newUsersDAO.isUserValid("aisudiaudsa","asjhdvasdas");
                break;
        }

        System.out.println("Update result: " + result);

        boolean updated = (result.size() > 0 && "Success".equalsIgnoreCase(result.get(0).get("status")) ||
                (result.size() > 1 && "Success".equalsIgnoreCase(result.get(1).get("status"))));
        System.out.println("Update status: " + updated);
        String response = updated
                ? "{\"status\": \"ok\"}"
                : "{\"status\": \"fail\"}";
        System.out.println("Response: " + response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<HashMap<String, String>> users = newUsersDAO.getAllUsers();
        System.out.println("Retrieved users: " + users);

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < users.size()-1; i++) {
            Map<String, String> ex = users.get(i);
            String name = ex.getOrDefault("login", "").replace("\"", "\\\"");
            String description = ex.getOrDefault("userType", "").replace("\"", "\\\"");
            jsonBuilder.append("{\"login\": \"").append(name).append("\", ");
            jsonBuilder.append("\"userType\": \"").append(description).append("\"}");
            if (i != users.size() - 1) {
                jsonBuilder.append(", ");
            }
        }

        jsonBuilder.append("]");

        String jsonResponse = jsonBuilder.toString();

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }

        System.out.println("Response sent: " + jsonResponse);
    }
}
