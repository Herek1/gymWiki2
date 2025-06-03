package gym.client;

import java.io.*;
import java.net.HttpURLConnection;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;

@WebServlet(name = "helloServlet", urlPatterns = {"/site", "/hello-servlet"})

public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World - test after docker!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        createPage(request, response);
    }

    private void createPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        TIUser user = (TIUser) session.getAttribute("user");
        if (user == null) {
            user = new TIUser();
            session.setAttribute("user", user);
        }

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();

        String page = request.getParameter("page");
        String layout = Tools.getLayout("index.html", context);
        layout = Tools.fill(layout, "HEADER", "pages/header.html", context);

        if("logowanie".equals(page)) {
            layout = Tools.fill(layout, "BODY", "pages/login.html", context);
        }else if ("wylogowanie".equals(page)) {
            layout = Tools.fill(layout, "BODY", "pages/logout.html", context);
        }else if("rejestracja".equals(page)) {
            layout = Tools.fill(layout, "BODY", "pages/rejestracja.html", context);
        } else {
            layout = Tools.fill(layout, "BODY", "pages/body.html", context);
        }

        if (user.getPrivilege().equals("demo")) {
            layout = layout.replace("[[LOGOWANIE]]",
                    "<li><a href=\"site?page=logowanie\">Logowanie</a></li>" +
                            "<li><a href=\"site?page=rejestracja\">Rejestracja</a></li>");
        } else {
            layout = layout.replace("[[LOGOWANIE]]", "<li><a href=\"site?page=wylogowanie\">Wylogowanie</a></li>");
        }

        if(user.getPrivilege().equals("user")){
            layout = layout.replace("[[LISTA_CWICZEN]]", Tools.fetchExercises());
        } else if (user.getPrivilege().equals("admin")) {
            layout = layout.replace("[[LISTA_CWICZEN]]",
                    "Link dodaj ćwiczenie");
        } else {
            layout = layout.replace("[[LISTA_CWICZEN]]", Tools.fetchDemoExercises());

        }

        layout = Tools.fill(layout, "FOOTER", "pages/footer.html", context);
        out.println(layout);
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        TIUser user = (TIUser) session.getAttribute("user");

        if (user == null) {
            user = new TIUser();
            session.setAttribute("user", user);
        }

        if (request.getParameter("potwierdzLogin") != null && user.getPrivilege().equals("demo")) {
            URL url = new URL("http://localhost:8090/auth");
            //URL url = new URL("http://server:8090/auth");
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

            if (json.contains("\"privilege\": \"user\"")) {
                user.setLogin(login);
                user.setPrivilege("user");
                System.out.println("User logged in as user");
            } else if (json.contains("\"privilege\": \"admin\"")) {
                user.setLogin(login);
                user.setPrivilege("admin");
                System.out.println("User logged in as admin");
            }else{
                System.out.println("Login failed, setting to demo");
            }
        }

        if (request.getParameter("potwierdzWylogowanie") != null && user.getPrivilege() != "demo") {
            user.setLogin("");
            user.setPrivilege("demo");
        }

        createPage(request, response);
    }


    public void destroy() {
    }


}