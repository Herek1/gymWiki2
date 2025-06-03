package main;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import database.dao.ExcersiseDAO;

import java.io.IOException;
import java.io.OutputStream;

public class ExerciseHandler implements HttpHandler {
    ExcersiseDAO excersiseDAO;
    public ExerciseHandler(ExcersiseDAO excersiseDAO) {
        this.excersiseDAO = excersiseDAO;

    }
    @Override
    public void handle(HttpExchange exchange) throws IOException, IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            exchange.sendResponseHeaders(405, -1); // Method Not Allowed
            return;
        }

        String jsonResponse = """
        [
            {"name": "Squat", "instruction": "Stand with feet shoulder-width apart and bend knees."},
            {"name": "Bench Press", "instruction": "Lie on bench, lower bar to chest, push back up."},
            {"name": "Deadlift", "instruction": "Lift bar from ground to hips while keeping back straight."}
        ]
        """;

        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, jsonResponse.length());

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(jsonResponse.getBytes());
        }
    }
}
