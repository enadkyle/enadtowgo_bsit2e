package org.example.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        String[] ddl = {
                "CREATE TABLE IF NOT EXISTS USERS (" +
                        " Users_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Username TEXT NOT NULL UNIQUE," +
                        " Password TEXT NOT NULL," +
                        " Full_Name TEXT NOT NULL," +
                        " Role TEXT NOT NULL," +
                        " Status TEXT NOT NULL DEFAULT 'PENDING')",

                "CREATE TABLE IF NOT EXISTS CUSTOMERS (" +
                        " Customer_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Customer_Name TEXT NOT NULL," +
                        " Contact_Number TEXT," +
                        " Address TEXT)",

                "CREATE TABLE IF NOT EXISTS DRIVERS (" +
                        " Driver_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Driver_Name TEXT NOT NULL," +
                        " Contact_Number TEXT," +
                        " License_Number TEXT," +
                        " Availability_Status TEXT)",

                "CREATE TABLE IF NOT EXISTS VEHICLES (" +
                        " Vehicle_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Customer_ID INTEGER NOT NULL," +
                        " Plate_Number TEXT NOT NULL," +
                        " Vehicle_Type TEXT," +
                        " Vehicle_Model TEXT," +
                        " FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS(Customer_ID))",

                "CREATE TABLE IF NOT EXISTS TOWING_REQUESTS (" +
                        " Request_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " Customer_ID INTEGER NOT NULL," +
                        " Vehicle_ID INTEGER NOT NULL," +
                        " Driver_ID INTEGER," +
                        " Request_Date TEXT DEFAULT CURRENT_TIMESTAMP," +
                        " Pickup_Location TEXT," +
                        " Destination TEXT," +
                        " Service_Status TEXT," +
                        " Service_Fee REAL," +
                        " FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS(Customer_ID)," +
                        " FOREIGN KEY (Vehicle_ID) REFERENCES VEHICLES(Vehicle_ID)," +
                        " FOREIGN KEY (Driver_ID) REFERENCES DRIVERS(Driver_ID))",

                "CREATE TABLE IF NOT EXISTS activity_logs (" +
                        " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " user_id INTEGER NOT NULL," +
                        " action_type TEXT NOT NULL," +
                        " description TEXT NOT NULL," +
                        " timestamp DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        " FOREIGN KEY (user_id) REFERENCES USERS(Users_ID) ON DELETE CASCADE)"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            dropOutdatedActivityLogs(conn);
            for (String sql : ddl) {
                st.execute(sql);
            }
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }

    // The old Deliverable 5 table had different columns (Action, Date_Time...).
    // CREATE TABLE IF NOT EXISTS would keep that old table, so drop it once
    // if it does not have the new action_type column.
    private static void dropOutdatedActivityLogs(Connection conn) throws SQLException {
        boolean exists = false;
        boolean hasNewSchema = false;

        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA table_info(activity_logs)")) {
            while (rs.next()) {
                exists = true;
                if ("action_type".equalsIgnoreCase(rs.getString("name"))) {
                    hasNewSchema = true;
                }
            }
        }

        if (exists && !hasNewSchema) {
            try (Statement st = conn.createStatement()) {
                st.execute("DROP TABLE activity_logs");
            }
        }
    }
}
