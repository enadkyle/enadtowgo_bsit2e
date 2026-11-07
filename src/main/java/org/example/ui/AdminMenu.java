package org.example.ui;

import org.example.dao.ActivityLogDao;
import org.example.dao.UserDao;
import org.example.model.ActivityLog;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;
    private final User user;
    private final ActivityLogDao logDao = new ActivityLogDao();

    private static final List<String> VALID_ROLES = List.of("ADMIN", "DRIVER", "STAFF");
    private static final List<String> VALID_STATUSES = List.of("APPROVED", "PENDING");

    public AdminMenu(UserDao userDao, Scanner scanner, User user) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.user = user;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== ADMIN MENU ===");
            System.out.println("1. View All System Users");
            System.out.println("2. View Pending Registrations");
            System.out.println("3. Approve User Account");
            System.out.println("4. Update User Role/Status");
            System.out.println("5. View System Audit Logs");
            System.out.println("6. Logout");

            int choice = readIntInput("Choose an option: ");

            switch (choice) {
                case 1:
                    printUserTable(userDao.getAllUsers());
                    break;
                case 2:
                    printUserTable(userDao.getPendingUsers());
                    break;
                case 3:
                    approveUser();
                    break;
                case 4:
                    updateUser();
                    break;
                case 5:
                    viewAuditLogs();
                    break;
                case 6:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void approveUser() {
        List<User> pending = userDao.getPendingUsers();
        if (pending.isEmpty()) {
            System.out.println("No pending accounts to approve.");
            return;
        }
        printUserTable(pending);

        while (true) {
            System.out.print("Enter Target User ID to approve (0 to cancel): ");
            String input = scanner.nextLine().trim();

            int id;
            try {
                id = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            if (id == 0) {
                System.out.println("Approval cancelled.");
                return;
            }

            boolean inPending = false;
            for (User u : pending) {
                if (u.usersId() == id) {
                    inPending = true;
                    break;
                }
            }
            if (!inPending) {
                System.out.println("ID " + id + " is not in the pending list. Try again.");
                continue;
            }

            if (userDao.approveUser(id)) {
                System.out.println("SUCCESS: User ID " + id + " has been APPROVED.");
                logDao.logAction(user.usersId(), "USER_APPROVED",
                        "Admin " + user.usersId() + " approved account for User ID " + id);
            } else {
                System.out.println("Approval failed. Please try again.");
            }
            return;
        }
    }

    // ---------- NEW for Deliverable 7 ----------
    private void updateUser() {
        List<User> users = userDao.getAllUsers();
        printUserTable(users);

        int targetUserId = readIntInput("Enter Target User ID to update (0 to cancel): ");
        if (targetUserId == 0) {
            System.out.println("Update cancelled.");
            return;
        }

        if (targetUserId == user.usersId()) {
            System.out.println("WARNING: Modifying the currently active admin account is restricted.");
            return;
        }

        boolean exists = false;
        for (User u : users) {
            if (u.usersId() == targetUserId) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            System.out.println("No user found with ID " + targetUserId + ".");
            return;
        }

        String newRole = readValidRole();
        if (newRole == null) {
            System.out.println("Update cancelled.");
            return;
        }

        String newStatus = readValidStatus();
        if (newStatus == null) {
            System.out.println("Update cancelled.");
            return;
        }

        if (userDao.updateUserRoleAndStatus(targetUserId, newRole, newStatus)) {
            System.out.println("SUCCESS: User ID " + targetUserId
                    + " updated to role " + newRole + " and status " + newStatus + ".");
            logDao.logAction(user.usersId(), "USER_UPDATED",
                    "Admin " + user.usersId() + " changed User " + targetUserId
                            + " to " + newRole + "/" + newStatus);
        } else {
            System.out.println("Update failed. Please try again.");
        }
    }

    private int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // Returns null if the admin enters 0 to cancel
    private String readValidRole() {
        while (true) {
            System.out.print("Enter new role " + VALID_ROLES + " (0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_ROLES.contains(input)) {
                return input;
            }
            System.out.println("Invalid role. Choose from: " + VALID_ROLES);
        }
    }

    // Returns null if the admin enters 0 to cancel
    private String readValidStatus() {
        while (true) {
            System.out.print("Enter new status " + VALID_STATUSES + " (0 to cancel): ");
            String input = scanner.nextLine().trim().toUpperCase();
            if (input.equals("0")) {
                return null;
            }
            if (VALID_STATUSES.contains(input)) {
                return input;
            }
            System.out.println("Invalid status. Choose from: " + VALID_STATUSES);
        }
    }

    // ---------- NEW for Deliverable 8 ----------
    private void viewAuditLogs() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== SYSTEM AUDIT LOGS ===");
            System.out.println("1. View All System Logs");
            System.out.println("2. Filter Logs by User ID");
            System.out.println("3. Back");

            int choice = readIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    printLogTable(logDao.getAllLogs());
                    break;
                case 2:
                    int userId = readIntInput("Enter User ID to filter: ");
                    printLogTable(logDao.getLogsByUserId(userId));
                    break;
                case 3:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void printLogTable(List<ActivityLog> logs) {
        String format = "%-5s | %-8s | %-18s | %-35s | %-20s%n";
        System.out.printf(format, "ID", "User ID", "Action Type", "Description", "Timestamp");
        System.out.println("-".repeat(97));

        if (logs.isEmpty()) {
            System.out.println("No logs found.");
            return;
        }

        for (ActivityLog log : logs) {
            String desc = log.description();
            if (desc.length() > 35) {
                desc = desc.substring(0, 32) + "...";
            }
            System.out.printf(format, log.id(), log.userId(), log.actionType(), desc, log.timestamp());
        }
    }

    private void printUserTable(List<User> users) {
        String border = "+-------+-----------------+--------------+------------+";
        System.out.println(border);
        System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n", "ID", "Username", "Role", "Status");
        System.out.println(border);

        if (users.isEmpty()) {
            System.out.printf("| %-49s |%n", "No users found.");
        } else {
            for (User u : users) {
                System.out.printf("| %-5s | %-15s | %-12s | %-10s |%n",
                        u.usersId(), u.username(), u.role(), u.status());
            }
        }
        System.out.println(border);
    }
}