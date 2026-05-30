package ru.mirea.khrechkorv.mireaproject.ui.Firebase;

public class ApiResponse {
    private boolean success;
    private String message;
    private UserProfile data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public UserProfile getData() { return data; }
}
