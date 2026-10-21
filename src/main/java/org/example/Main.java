package org.example;

import org.example.dao.DatabaseInitializer;
import org.example.dao.UserDao;
import org.example.ui.Authentication;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseInitializer.initialize();
        } catch (RuntimeException e) {
            System.out.println("Cannot start application: " + e.getMessage());
            return;
        }

        UserDao userDao = new UserDao();
        Scanner scanner = new Scanner(System.in);
        new Authentication(userDao, scanner).start();
        scanner.close();
    }
}