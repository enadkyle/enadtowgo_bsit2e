package org.example.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.example.model.ActivityLog;

public class ActivityLogDao {

    public void logAction(int userId, String actionType, String description) {
        String sql = "INSERT INTO activity_logs (user_id, action_type, description) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, actionType);
            ps.setString(3, description);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error writing activity log: " + e.getMessage());
        }
    }

    public List<ActivityLog> getAllLogs() {
        String sql = "SELECT id, user_id, action_type, description, timestamp "
                + "FROM activity_logs ORDER BY timestamp DESC, id DESC";
        return runQuery(sql, null);
    }

    public List<ActivityLog> getLogsByUserId(int userId) {
        String sql = "SELECT id, user_id, action_type, description, timestamp "
                + "FROM activity_logs WHERE user_id = ? ORDER BY timestamp DESC, id DESC";
        return runQuery(sql, userId);
    }

    private List<ActivityLog> runQuery(String sql, Integer userId) {
        List<ActivityLog> logs = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (userId != null) {
                ps.setInt(1, userId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    logs.add(new ActivityLog(
                            rs.getInt("id"),
                            rs.getInt("user_id"),
                            rs.getString("action_type"),
                            rs.getString("description"),
                            rs.getString("timestamp")
                    ));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error loading activity logs: " + e.getMessage());
        }

        return logs;
    }
}
