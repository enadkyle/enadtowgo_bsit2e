package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.Scanner;

public class Authentication {
    private static final String[] ROLES = {"ADMIN", "STAFF", "DRIVER"};
    private static final String APP_TITLE = "TOWGO TOWING SERVICE";

    private final UserDao userDao;
    private final Scanner scanner;

    public Authentication(UserDao userDao, Scanner scanner) {
        this.userDao = userDao;
        this.scanner = scanner;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== " + APP_TITLE + " MAIN MENU ===");
            System.out.println("1. Register Account");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            switch (scanner.nextLine().trim()) {
                case "1": register(); break;
                case "2": login(); break;
                case "3": System.out.println("Goodbye!"); running = false; break;
                default:  System.out.println("Invalid option.");
            }
        }
    }

    private void register() {
        System.out.print("Full name: ");
        String fullName = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        System.out.println("Select role:");
        for (int i = 0; i < ROLES.length; i++) {
            System.out.println((i + 1) + ". " + ROLES[i]);
        }
        System.out.print("Role: ");
        int choice;
        try {
            choice = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }
        if (choice < 1 || choice > ROLES.length) {
            System.out.println("Invalid role. Registration cancelled.");
            return;
        }

        User user = new User(fullName, username, password, ROLES[choice - 1], "PENDING");
        if (userDao.register(user)) {
            System.out.println("Registered! Status: PENDING (awaiting approval).");
        } else {
            System.out.println("Registration failed (username may already exist).");
        }
    }

    private void login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User user = userDao.login(username, password);
        if (user == null) {
            System.out.println("Invalid username or password.");
            return;
        }
        if ("PENDING".equalsIgnoreCase(user.status())) {
            System.out.println("Your account is still pending approval.");
            return;
        }
        routeByRole(user);
    }

    private void routeByRole(User user) {
        System.out.println("Welcome, " + user.fullName() + "!");
        switch (user.role().toUpperCase()) {
            case "ADMIN":  new AdminMenu(userDao, scanner, user).show(); break;
            case "STAFF":  new StaffMenu(userDao, scanner, user).show(); break;
            case "DRIVER": new DriverMenu(userDao, scanner, user).show(); break;
            default: System.out.println("Unknown role: " + user.role());
        }
    }
}