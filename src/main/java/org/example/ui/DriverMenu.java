package org.example.ui;

import org.example.dao.UserDao;
import org.example.model.User;

import java.util.Scanner;

public class DriverMenu {
    private final UserDao userDao;
    private final Scanner scanner;
    private final User user;

    public DriverMenu(UserDao userDao, Scanner scanner, User user) {
        this.userDao = userDao;
        this.scanner = scanner;
        this.user = user;
    }

    public void show() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== DRIVER MENU ===");
            System.out.println("1. (features coming soon)");
            System.out.println("2. Logout");
            System.out.print("Choose an option: ");
            if (scanner.nextLine().trim().equals("2")) {
                running = false;
            }
        }
    }
}