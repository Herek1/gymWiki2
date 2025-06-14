package gym.client;

import jakarta.servlet.ServletContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Tools {

    public static String getLayout(String file, ServletContext context) throws IOException {
        StringBuilder out = new StringBuilder();
        try (InputStream is = context.getResourceAsStream("/" + file)) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String text;
                    while ((text = reader.readLine()) != null) {
                        out.append(text).append("\n");
                    }
                }
            } else {
                System.err.println("File is missing: " + file);
                out.append("File is missing: ").append(file);
            }
        }
        return out.toString();
    }

    public static String fill(String layout, String marker, String file, ServletContext context) throws IOException {
        StringBuilder out = new StringBuilder();
        try (InputStream is = context.getResourceAsStream("/" + file)) {
            if (is != null) {
                try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
                     BufferedReader reader = new BufferedReader(isr)) {
                    String text;
                    while ((text = reader.readLine()) != null) {
                        out.append(text).append("\n");
                    }
                }
            } else {
                System.err.println("File is missing: " + file);
                out.append("File is missing: ").append(file);
            }
        }
        return layout.replace("[[" + marker + "]]", out.toString());
    }

    public static String fetchExercises() {
        try {
            URL url = new URL("http://localhost:8090/exercises");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            InputStream is = con.getInputStream();
            String json = new String(is.readAllBytes());

            String jsonArray = json.trim();
            if (jsonArray.startsWith("[")) jsonArray = jsonArray.substring(1);
            if (jsonArray.endsWith("]")) jsonArray = jsonArray.substring(0, jsonArray.length() - 1);

            String[] entries = jsonArray.split("\\},\\s*\\{");

            StringBuilder html = new StringBuilder();
            for (String entry : entries) {
                entry = entry.replaceAll("[\\[\\]{}]", ""); // remove leftover braces
                String name = entry.replaceAll(".*\"name\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                String instr = entry.replaceAll(".*\"instruction\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                html.append("<tr><td>").append(name).append("</td><td>").append(instr).append("</td></tr>");
            }

            return "<table><tr><th>Nazwa ćwiczenia</th><th>Opis</th></tr>" + html + "</table>";

        } catch (IOException e) {
            return "<p>Nie udało się załadować ćwiczeń.</p>";
        }
    }

    public static String fetchUsers() {
        try {
            URL url = new URL("http://localhost:8090/users");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            InputStream is = con.getInputStream();
            String json = new String(is.readAllBytes());

            String jsonArray = json.trim();
            if (jsonArray.startsWith("[")) jsonArray = jsonArray.substring(1);
            if (jsonArray.endsWith("]")) jsonArray = jsonArray.substring(0, jsonArray.length() - 1);

            String[] entries = jsonArray.split("\\},\\s*\\{");

            StringBuilder html = new StringBuilder();
            for (String entry : entries) {
                entry = entry.replaceAll("[\\[\\]{}]", ""); // remove leftover braces
                String name = entry.replaceAll(".*\"login\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                String instr = entry.replaceAll(".*\"userType\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                html.append("<tr><td>").append(name).append("</td><td>").append(instr).append("</td></tr>");
            }

            return "<table><tr><th>Nazwa użytkownika</th><th>Rola</th></tr>" + html + "</table>";

        } catch (IOException e) {
            return "<p>Nie udało się załadować userów.</p>";
        }
    }

    public static String fetchDemoExercises() {
        try {
            URL url = new URL("http://localhost:8090/exercises");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            InputStream is = con.getInputStream();
            String json = new String(is.readAllBytes());

            String jsonArray = json.trim();
            if (jsonArray.startsWith("[")) jsonArray = jsonArray.substring(1);
            if (jsonArray.endsWith("]")) jsonArray = jsonArray.substring(0, jsonArray.length() - 1);

            String[] entries = jsonArray.split("\\},\\s*\\{");

            StringBuilder html = new StringBuilder();
            for (String entry : entries) {
                entry = entry.replaceAll("[\\[\\]{}]", ""); // remove leftover braces
                String name = entry.replaceAll(".*\"name\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                String instr = entry.replaceAll(".*\"instruction\"\\s*:\\s*\"([^\"]+)\".*", "$1");
                String demoInstr = instr.length() > 25
                        ? instr.substring(0, 25) + "... [Zaloguj się, by zobaczyć cały opis]"
                        : instr;
                html.append("<tr><td>").append(name).append("</td><td>").append(demoInstr).append("</td></tr>");
            }

            return "<table><tr><th>Nazwa ćwiczenia</th><th>Opis</th></tr>" + html + "</table>";

        } catch (IOException e) {
            return "<p>Nie udało się załadować ćwiczeń.</p>";
        }
    }

}
