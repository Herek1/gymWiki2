package main;

import com.sun.net.httpserver.HttpServer;
import database.dao.ExcersiseDAO;
import database.dao.UsersDAO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) throws IOException {

        database.Engine myEngine = new database.Engine();
        myEngine.start();
        Connection newConnection = myEngine.returnConnection();
        UsersDAO newUsersDAO = new UsersDAO(newConnection);
        ExcersiseDAO excersiseDAO = new ExcersiseDAO(newConnection);

        HttpServer server = HttpServer.create(new InetSocketAddress(8090), 0);
        server.createContext("/auth", new AuthHandler(newUsersDAO));
        server.createContext("/exercises", new ExerciseHandler(excersiseDAO));
        server.setExecutor(null);
        System.out.println("Login server running on http://localhost:8090/");
        server.start();
    }
}
