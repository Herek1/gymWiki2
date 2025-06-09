package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.dao.ExcersiseDAO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ExerciseHandler implements HttpHandler {
    private final ExcersiseDAO excersiseDAO;

    public ExerciseHandler(ExcersiseDAO excersiseDAO) {
        this.excersiseDAO = excersiseDAO;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String body = new String(exchange.getRequestBody().readAllBytes());
        System.out.println("===== Incoming Request =====");
        System.out.println("Method: " + method);
        System.out.println("Raw body: " + body);

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
        System.out.println("===== Response Sent =====");
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        // In the future, you can differentiate types of GET via query params
        // String query = exchange.getRequestURI().getQuery();

        String jsonResponse = """
        [
            {"name": "Squat", "instruction": "Stand with feet shoulder-width apart and bend knees."},
            {"name": "Bench Press", "instruction": "Lie on bench, lower bar to chest, push back up."},
            {"name": "Deadlift", "instruction": "Lift bar from ground to hips while keeping back straight."}
        ]
        """;

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> formData = parseFormData(body);

        String name = formData.getOrDefault("name", "");
        String description = formData.getOrDefault("description", "");
        String response;
        if (!name.isEmpty() && !description.isEmpty()) {
            System.out.println("Adding exercise: " + name + " - " + description);
            //add to db dao
            response = "{\"status\":\"ok\"}";
            exchange.sendResponseHeaders(200, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } else {
            response = "{\"status\":\"fail\", \"reason\":\"Missing fields\"}";
            exchange.sendResponseHeaders(400, response.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        }
        System.out.println("Response sent: " + response);
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
