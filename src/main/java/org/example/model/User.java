package org.example.model;

public record User(
        int usersId,
        String fullName,
        String username,
        String password,
        String role,
        String status
) {
    public User(String fullName, String username, String password, String role, String status) {
        this(0, fullName, username, password, role, status);
    }
}