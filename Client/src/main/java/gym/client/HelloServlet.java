package gym.client;

import java.io.*;
import java.net.HttpURLConnection;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.net.URL;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.concurrent.SynchronousQueue;

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
        if (page == null || page.isEmpty()) {
            page = "body";
        }

        switch (page) {
            case "logowanie":
                layout = Tools.fill(layout, "BODY", "pages/login.html", context);
                break;
            case "wylogowanie":
                layout = Tools.fill(layout, "BODY", "pages/logout.html", context);
                break;
            case "rejestracja":
                layout = Tools.fill(layout, "BODY", "pages/rejestracja.html", context);
                break;
            case "dodaj_cwiczenie":
                layout = Tools.fill(layout, "BODY", "pages/dodaj_cwiczenie.html", context);
                break;
            case "zarzadzaj_uzytkownikami":
                layout = Tools.fill(layout, "BODY", "pages/zarzadzaj_uzytkownikami.html", context);
                layout = layout.replace("[[LISTA_UZYTKOWNIKOW]]", Tools.fetchUsers());
                break;
            case "profil":
                layout = Tools.fill(layout, "BODY", "pages/profil.html", context);
                break;
            case "editProfil":
                layout = Tools.fill(layout, "BODY", "pages/editProfil.html", context);
                break;
            default:
                layout = Tools.fill(layout, "BODY", "pages/body.html", context);
                break;
        }

        if (user.getPrivilege().equals("demo")) {
            layout = layout.replace("[[LOGOWANIE]]",
                    "<a href=\"site?page=logowanie\">Logowanie</a>" +
                            "<a href=\"site?page=rejestracja\">Rejestracja</a>");
        } else {
            layout = layout.replace("[[LOGOWANIE]]", "" +
                    "<a href=\"site?page=wylogowanie\">Wylogowanie</a>" +
                    "<a href=\"site?page=editProfil\">Mój profil</a>");
        }
        if (user.getPrivilege().equals("admin")) {
            layout = layout.replace("[[ADMIN_LINKS]]", "" +
                    "<a href=\"site?page=dodaj_cwiczenie\">Zarządzaj ćwiczeniami</a>" +
                    "<a href=\"site?page=zarzadzaj_uzytkownikami\">Zarządzaj użytkownikami</a>");
        } else {
            layout = layout.replace("[[ADMIN_LINKS]]", "");
        }
        String searchTerm = request.getParameter("szukaj");
        if(user.getPrivilege().equals("demo")){
            layout = layout.replace("[[LISTA_CWICZEN]]", Tools.fetchDemoExercises());
            layout = layout.replace("[[LOGGED_IN_LINKS]]", "");
        } else {
            layout = layout.replace("[[LISTA_CWICZEN]]", Tools.fetchExercises());
            layout = layout.replace("[[LOGGED_IN_LINKS]]", "<a href=\"site?page=profil\">Profil</a>");

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
            RequestHandler.handleLogin(response, login, password, user);
        }

        if (request.getParameter("potwierdzWylogowanie") != null && user.getPrivilege() != "demo") {
            user.setLogin("");
            user.setPrivilege("demo");
        }

        if (request.getParameter("dodaj_cwiczenie") != null && user.getPrivilege().equals("admin")) {
            RequestHandler.addExercise(request, response);
        }

        if(request.getParameter("modyfikuj_cwiczenie") != null && user.getPrivilege().equals("admin")){
            RequestHandler.modifyExercise(request, response);
        }

        if(request.getParameter("usun_cwiczenie") != null && user.getPrivilege().equals("admin")) {
            RequestHandler.removeExercise(request, response);
        }

        if (request.getParameter("dodajUzytkownika") != null && user.getPrivilege().equals("admin")) {
            RequestHandler.addUser(request, response);
        }

        if (request.getParameter("usunUzytkownika") != null && user.getPrivilege().equals("admin")) {
            RequestHandler.removeUser(request, response);
        }

        if(request.getParameter("modyfikujUżytkownika") != null && user.getPrivilege().equals("admin")) {
            RequestHandler.editUser(request, response);
        }

        if (request.getParameter("rejestracjaUzytkownika") != null && user.getPrivilege().equals("demo")) {
            RequestHandler.registerUser(request, response);
        }

        if(request.getParameter("edytujProfil") != null && user.getPrivilege() != "demo"){
            RequestHandler.editProfile(request, response, user);
        }

        if(request.getParameter("usunSwojegoUzytkownika") != null && user.getPrivilege() != "demo"){
            RequestHandler.removeOwnUser(request, response, user);
        }


        createPage(request, response);
    }


    public void destroy() {
    }


}