package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.dao.ExcersiseDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExerciseHandler implements HttpHandler {
    private final ExcersiseDAO excersiseDAO;
    List<HashMap<String, String>> exercises = new ArrayList<>();

    public ExerciseHandler(ExcersiseDAO excersiseDAO) {
        this.excersiseDAO = excersiseDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        //System.out.println("===== Incoming Request Exercise =====");
        //System.out.println("Method: " + method);
        System.out.println("Method: " + method + ", Request URI: " + exchange.getRequestURI());
        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
                case "DELETE":
                    handleDelete(exchange);
                    break;

            default:
                exchange.sendResponseHeaders(405, -1);
                break;// Method Not Allowed
        }
        //System.out.println("===== Response Sent =====");
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<HashMap<String, String>> exercises = excersiseDAO.getAllExcersises();
        System.out.println("Retrieved exercises: " + exercises);

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < exercises.size()-1; i++) {
            Map<String, String> ex = exercises.get(i);
            String name = ex.getOrDefault("name", "").replace("\"", "\\\"");
            String description = ex.getOrDefault("description", "").replace("\"", "\\\"");
            jsonBuilder.append("{\"name\": \"").append(name).append("\", ");
            jsonBuilder.append("\"instruction\": \"").append(description).append("\"}");
            if (i != exercises.size() - 1) {
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

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Raw body: " + body + ", Request URI: " + exchange.getRequestURI());

        String response;
        String name = "";
        String description = "";
        String requestType = "";
        String[] parts = body.split("&");
        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("req")) requestType = kv[1];
                if (kv[0].equals("name")) name = kv[1];
                if (kv[0].equals("description")) description = kv[1];
            }
        }

        switch (requestType) {
            case "add":
                if (!name.isEmpty() && !description.isEmpty()) {
                    excersiseDAO.createExcersise(name, description);
                    response = "{\"status\":\"ok\"}";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                } else {
                    response = "{\"status\":\"fail\", \"reason\":\"Missing fields\"}";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                }
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                break;
            case "modify":
                if (!name.isEmpty() && !description.isEmpty()) {
                    excersiseDAO.updateExcersiseByName(name, name, description);
                    response = "{\"status\":\"ok\"}";
                    exchange.sendResponseHeaders(200, response.getBytes().length);
                } else {
                    response = "{\"status\":\"fail\", \"reason\":\"Missing fields\"}";
                    exchange.sendResponseHeaders(400, response.getBytes().length);
                }
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
                break;
            default:
                response = "{\"status\":\"fail\", \"reason\":\"Invalid request type\"}";
                exchange.sendResponseHeaders(400, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("Raw body: " + body + ", Request URI: " + exchange.getRequestURI());

        String response;
        String name = "";
        String[] parts = body.split("&");
        for (String part : parts) {
            String[] kv = part.split("=");
            if (kv.length == 2) {
                if (kv[0].equals("name")) name = kv[1];
            }
        }
        excersiseDAO.deleteExcersise(name);
        response = "{\"status\":\"ok\"}";
        System.out.println("Response: " + response);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    private Map<String, String> parseFormData(String form) {
        Map<String, String> map = new HashMap<>();
        String[] pairs = form.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                map.put(key, value);
            }
        }
        return map;
    }
}
