package org.example.dao;

import java.sql.Connection;
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

                "CREATE TABLE IF NOT EXISTS ACTIVITY_LOGS (" +
                        " Log_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                        " User_ID INTEGER," +
                        " Driver_ID INTEGER," +
                        " Action TEXT NOT NULL," +
                        " Date_Time TEXT DEFAULT CURRENT_TIMESTAMP," +
                        " Description TEXT," +
                        " FOREIGN KEY (User_ID) REFERENCES USERS(Users_ID)," +
                        " FOREIGN KEY (Driver_ID) REFERENCES DRIVERS(Driver_ID))"
        };

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            for (String sql : ddl) {
                st.execute(sql);
            }
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            throw new RuntimeException("Database initialization failed: " + e.getMessage(), e);
        }
    }
}