package database.dao;

import database.utils.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersDAO extends AbstractDAO {
    String salt;

    public UsersDAO(Connection conn) {
        super(conn);
        this.salt = BCrypt.gensalt();
    }

    public List<HashMap<String, String>> createUser(String login, String password, String userType, String name, String surname) {
        String query = "INSERT INTO users (login, password, user_type, name, surname) VALUES (?, ?, ?, ?, ?)";
        List<HashMap<String, String>> result = new ArrayList<>();
        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);
            stmt.setString(2, BCrypt.hashpw(password, this.salt));
            stmt.setString(3, userType);
            stmt.setString(4, name);
            stmt.setString(5, surname);
            stmt.executeUpdate();
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        result.add(staticInfo1);
        return result;
    }

    public List<HashMap<String, String>> updateUserPassword(String login, String newPassword) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "UPDATE users SET password = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, BCrypt.hashpw(newPassword, this.salt));
            stmt.setString(2, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User password was not updated");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.add(staticInfo1);
        return infoList;
    }

    public List<HashMap<String, String>> deleteUser(String login) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "DELETE FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User was not deleted");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.add(staticInfo1);
        return infoList;
    }

    public List<HashMap<String, String>> isUserValid(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "SELECT login, password FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                if (rs.next()) {
                    String hashedPassword = rs.getString("password");

                    if (BCrypt.checkpw(password, hashedPassword)) {
                        user.put("exists", "true");
                    } else {
                        staticInfo1.replace(message.getHashIdStatus(), "error");
                        staticInfo1.replace(message.getHashIdUserFriendlyError(), "Invalid login or password");
                    }
                } else {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "User does not exist");
                }
                userList.add(user);
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        userList.add(staticInfo1);
        return userList;
    }


    public List<HashMap<String, String>> getUser(String login, String password) {
        List<HashMap<String, String>> userList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "SELECT user_type, name, surname FROM users WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, login);

            try (ResultSet rs = stmt.executeQuery()) {
                HashMap<String, String> user = new HashMap<>();
                while (rs.next()) {
                    user.put("userType", rs.getString("user_type"));
                    user.put("name", rs.getString("name"));
                    user.put("surname", rs.getString("surname"));
                }
                if (user.isEmpty()) {
                    staticInfo1.replace(message.getHashIdStatus(), "error");
                    staticInfo1.replace(message.getHashIdUserFriendlyError(), "There is no user for that password and login");
                } else {
                    userList.add(user);
                }
            }
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        userList.add(staticInfo1);
        return userList;
    }

    public List<HashMap<String, String>> getAllUsers() {
        String query = "SELECT login, user_type, name, surname FROM users";
        List<HashMap<String, String>> results = new ArrayList<>();

        HashMap<String, String> staticInfo = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HashMap<String, String> record = new HashMap<>();
                    record.put("login", rs.getString("login"));
                    record.put("userType", rs.getString("user_type"));
                    record.put("name", rs.getString("name"));
                    record.put("surname", rs.getString("surname"));
                    results.add(record);
                }
            }
        } catch (SQLException e) {
            staticInfo = errorHandler.handleSQLException(e, staticInfo, message);
        }

        results.add(staticInfo);
        return results;
    }

    public List<HashMap<String, String>> updateUserRole(String login, String newRole) {
        List<HashMap<String, String>> infoList = new ArrayList<>();

        HashMap<String, String> staticInfo1 = new HashMap<>(message.getDefaultErrorMessageAsHashMap());

        String query = "UPDATE users SET user_type = ? WHERE login = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newRole);
            stmt.setString(2, login);

            HashMap<String, String> staticInfo2 = new HashMap<>();
            if (stmt.executeUpdate() > 0) {
                staticInfo2.put("success", "true");
            } else {
                staticInfo1.replace(message.getHashIdStatus(), "error");
                staticInfo1.replace(message.getHashIdUserFriendlyError(), "User role was not updated");
            }
            infoList.add(staticInfo2);
        } catch (SQLException e) {
            staticInfo1 = errorHandler.handleSQLException(e, staticInfo1, message);
        }
        infoList.add(staticInfo1);
        return infoList;
    }


}