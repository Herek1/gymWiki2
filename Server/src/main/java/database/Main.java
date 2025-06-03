package database;

import database.dao.*;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.sql.Date;

public class Main {

    public static void main(String[] args) {

        /**/
        Engine myEngine = new Engine();
        myEngine.start();
        Connection newConnection = myEngine.returnConnection();
        UsersDAO newUsersDAO = new UsersDAO(newConnection);
        ExcersiseDAO excersiseDAO = new ExcersiseDAO(newConnection);

        List<HashMap<String, String>> testList = new ArrayList<>();
        List<HashMap<String, String>> testList2 = new ArrayList<>();

        testList = newUsersDAO.createUser("testu1", "t1", "user", "dname1", "dsurname1");
        testList = newUsersDAO.createUser("testu2", "t2", "admin", "dname2", "dsurname2");
        testList2 = excersiseDAO.createExcersise("name1", "desc1");
        testList2 = excersiseDAO.updateExcersiseByName("name1", "", "TEST1");

        testList = newUsersDAO.isUserValid("testu1", "t1");
        System.out.println(testList);
        //System.out.println(testList2);
    }
}
