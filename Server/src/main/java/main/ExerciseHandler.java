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

        switch (method) {
            case "GET":
                handleGet(exchange);
                break;
            case "POST":
                handlePost(exchange);
                break;
            default:
                exchange.sendResponseHeaders(405, -1); // Method Not Allowed
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
        //System.out.println("Raw body: " + body);

        Map<String, String> formData = parseFormData(body);

        String name = formData.getOrDefault("name", "");
        String description = formData.getOrDefault("description", "");
        String response;
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
        //System.out.println("Response sent: " + response);
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
