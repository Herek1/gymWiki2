package database;
import java.sql.Connection;
import java.util.LinkedHashMap;
import java.util.Map;

public class EngineStartup {
    private final Tables table = new Tables();
    private final Map<String, String> tablesToCreate = new LinkedHashMap<>();

    public void run(Connection engineConnection){

        tablesToCreate.put("users", """
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                login VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                user_type VARCHAR(50) NOT NULL,
                name VARCHAR(50) NULL,
                surname VARCHAR(50) NULL
            )
        """);

        tablesToCreate.put("excersise", """
            CREATE TABLE excersise (
                excersiseid SERIAL PRIMARY KEY,
                name VARCHAR(225) NOT NULL UNIQUE,
                description TEXT NOT NULL
            )
        """);

        for (Map.Entry<String, String> entry : tablesToCreate.entrySet()) {
            String tableName = entry.getKey();
            String createQuery = entry.getValue();

            if (!table.doesTableExist(engineConnection, tableName)) {
                table.createTable(createQuery, engineConnection);
            }
        }
    }

    public Map<String, String> returnMapOfTables(){
        return tablesToCreate;
    }
}
