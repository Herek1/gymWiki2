package database.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcersiseDAO extends AbstractDAO {

    public ExcersiseDAO(Connection conn) {
        super(conn);
    }

    public List<HashMap<String, String>> createExcersise(String name, String description) {
        String query = "INSERT INTO excersise (name, description) VALUES (?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.executeUpdate();

            HashMap<String, String> successInfo = new HashMap<>();
            successInfo.put("success", "true");
            result.add(successInfo);
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
            result.add(staticInfo);
        }

        return result;
    }

    public List<HashMap<String, String>> getAllExcersises() {
        String query = "SELECT excersiseid, name, description FROM excersise";
        List<HashMap<String, String>> results = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> record = new HashMap<>();
                    record.put("excersiseid", String.valueOf(rs.getInt("excersiseid")));
                    record.put("name", rs.getString("name"));
                    record.put("description", rs.getString("description"));
                    results.add(record);
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        results.add(staticInfo);
        return results;
    }

    public List<HashMap<String, String>> updateExcersiseByName(String currentName, String newName, String newDescription) {
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        StringBuilder queryBuilder = new StringBuilder("UPDATE excersise SET ");
        List<Object> params = new ArrayList<>();

        if (newName != null && !newName.isEmpty()) {
            queryBuilder.append("name = ?");
            params.add(newName);
        }

        if (newDescription != null && !newDescription.isEmpty()) {
            if (!params.isEmpty()) queryBuilder.append(", ");
            queryBuilder.append("description = ?");
            params.add(newDescription);
        }

        if (params.isEmpty()) {
            staticInfo.replace(message.getHashIdStatus(), "error");
            staticInfo.replace(message.getHashIdUserFriendlyError(), "No update values provided.");
            result.add(staticInfo);
            return result;
        }

        queryBuilder.append(" WHERE name = ?");
        params.add(currentName);

        String query = queryBuilder.toString();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            HashMap<String, String> status = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                status.put("success", "true");
            } else {
                staticInfo.replace(message.getHashIdStatus(), "error");
                staticInfo.replace(message.getHashIdUserFriendlyError(), "No excersise found with the given name.");
            }
            result.add(status);
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        result.add(staticInfo);
        return result;
    }

}
