package database;

import java.sql.Connection;
import java.sql.ResultSet;

public class Statement {
    private static java.sql.Statement statement;
    void setStatement(String query, Connection conn){
        try{
            Statement.statement = conn.createStatement();
            Statement.statement.executeUpdate(query);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
    ResultSet getStatement(String query, Connection conn) {
        try {
            Statement.statement = conn.createStatement();
            return Statement.statement.executeQuery(query);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
}

