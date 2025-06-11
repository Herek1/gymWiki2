package gym.client;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RequestHandler {
    public static void handleLogin(HttpServletResponse response, String login, String password, TIUser user) throws IOException {
        URL url = new URL("http://localhost:8090/auth");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "login=" + login + "&password=" + password;
        try (OutputStream os = con.getOutputStream()) {
            os.write(data.getBytes());
        }

        InputStream is = con.getInputStream();
        String json = new String(is.readAllBytes());
        System.out.println("Response from server: " + json);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (json.contains("\"privilege\": \"user\"")) {
            user.setLogin(login);
            user.setPrivilege("user");
            System.out.println("User logged in as user");
            out.println("<script>alert('You have logged in as USER');</script>");
        } else if (json.contains("\"privilege\": \"admin\"")) {
            user.setLogin(login);
            user.setPrivilege("admin");
            System.out.println("User logged in as admin");
            out.println("<script>alert('You have logged in as ADMIN');</script>");
        }else{
            System.out.println("Login failed, setting to demo");
            out.println("<script>alert('Login failed');</script>");
        }
    }

    public static void addExercise(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String name = request.getParameter("nazwa");
        String description = request.getParameter("opis");

        URL url = new URL("http://localhost:8090/exercises");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "name=" + name + "&description=" + description;
        try (OutputStream os = con.getOutputStream()) {
            os.write(data.getBytes());
        }

        InputStream is = con.getInputStream();
        String json = new String(is.readAllBytes());
        System.out.println("Response from server: " + json);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
    }

    public static void addUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("haslo");
        String userType = request.getParameter("rola");

        URL url = new URL("http://localhost:8090/users");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "login=" + login + "&password=" + password + "&userType=" + userType;
        try (OutputStream os = con.getOutputStream()) {
            os.write(data.getBytes());
        }

        InputStream is = con.getInputStream();
        String json = new String(is.readAllBytes());
        System.out.println("Response from server: " + json);
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if( json.contains("\"status\": \"ok\"")) {
            System.out.println("User added successfully");
            out.println("<script>alert('Added user: " + login + "');</script>");
        } else {
            System.out.println("Failed to add user");
            out.println("<script>alert('Failed to add user: " + login + "');</script>");
        }
    }

    public static void removeUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("usun_login");

        URL url = new URL("http://localhost:8090/users?login=" + login);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setDoOutput(false);

        int status = con.getResponseCode();
        InputStream is = (status >= 400) ? con.getErrorStream() : con.getInputStream();
        String json = new String(is.readAllBytes());
        System.out.println("Response from server: " + json);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        if (json.contains("\"status\": \"ok\"")) {
            System.out.println("User deleted successfully");
            out.println("<script>alert('User deleted successfully: " + login + "');</script>");
        } else {
            System.out.println("User deletion failed");
            out.println("<script>alert('Failed to delete user: " + login + "');</script>");
        }
    }

    public static void registerUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("username");
        String password = request.getParameter("password");
        String password2 = request.getParameter("password2");
        PrintWriter out = response.getWriter();


        if (login == null || password == null || password2 == null || !password.equals(password2)) {
            out.println("<script>alert('Failed to register user: " + login + "');</script>");
            return;
        }

        URL url = new URL("http://localhost:8090/users");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String data = "login=" + login + "&password=" + password;
        try (OutputStream os = con.getOutputStream()) {
            os.write(data.getBytes());
        }

        InputStream is = con.getInputStream();
        String json = new String(is.readAllBytes());
        System.out.println("Response from server: " + json);
        response.setContentType("text/html");


        if (json.contains("\"status\": \"ok\"")) {
            System.out.println("User registered successfully");
            out.println("<script>alert('User registered successfully: " + login + "');</script>");
        } else {
            System.out.println("User registration failed");
            out.println("<script>alert('Failed to register user: " + login + "');</script>");
        }
    }
}
