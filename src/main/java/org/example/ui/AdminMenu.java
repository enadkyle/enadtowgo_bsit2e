package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.List;
import java.util.Scanner;

public class AdminMenu {
    private final UserDao userDao;
    private final Scanner scanner;
    private final User user;

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
            System.out.println("5. Logout");

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