package org.example.model;

public class User {
    private int usersId;
    private String username;
    private String password;
    private String fullName;
    private String role;
    private String status;

    public User(String fullName, String username, String password, String role, String status) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
    }

    public User(int usersId, String fullName, String username, String password, String role, String status) {
        this(fullName, username, password, role, status);
        this.usersId = usersId;
    }

    public int getUsersId() { return usersId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
    public String getStatus() { return status; }
}