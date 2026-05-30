package ru.mirea.khrechkorv.mireaproject.ui.Firebase;

public class UserProfile {
    private String fullName;
    private String group;
    private String number;
    private String favoriteFilm;

    public UserProfile(String fullName, String group, String number, String favoriteFilm) {
        this.fullName = fullName;
        this.group = group;
        this.number = number;
        this.favoriteFilm = favoriteFilm;
    }

    // Getters and Setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }

    public String getFavoriteFilm() { return favoriteFilm; }
    public void setFavoriteFilm(String favoriteFilm) { this.favoriteFilm = favoriteFilm; }
}
