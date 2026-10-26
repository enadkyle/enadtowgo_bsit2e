package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.model.User;

public class UserDao {

    public boolean register(User user) {
        String sql = "INSERT INTO USERS (Username, Password, Full_Name, Role, Status) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.username());
            ps.setString(2, user.password());
            ps.setString(3, user.fullName());
            ps.setString(4, user.role());
            ps.setString(5, user.status());
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Register error: " + e.getMessage());
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM USERS WHERE Username = ? AND Password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
                return null;
            }

        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
            return null;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }

        return users;
    }

    public List<User> getPendingUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM USERS WHERE Status = 'PENDING'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error loading pending users: " + e.getMessage());
        }

        return users;
    }

    public boolean approveUser(int userId) {
        String sql = "UPDATE USERS SET Status = 'APPROVED' WHERE Users_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error approving user: " + e.getMessage());
            return false;
        }
    }

    // NEW for Deliverable 7
    public boolean updateUserRoleAndStatus(int userId, String newRole, String newStatus) {
        String sql = "UPDATE USERS SET Role = ?, Status = ? WHERE Users_ID = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newRole.toUpperCase());
            ps.setString(2, newStatus.toUpperCase());
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("Users_ID"),
                rs.getString("Full_Name"),
                rs.getString("Username"),
                rs.getString("Password"),
                rs.getString("Role"),
                rs.getString("Status")
        );
    }
}