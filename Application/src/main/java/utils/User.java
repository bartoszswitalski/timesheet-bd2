package utils;

public class User {
    private String id;
    private String login;
    private String password;
    private String role;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setID(String userID) {
        this.id = userID;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getID() {
        return this.id;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRole() {
        return this.role;
    }
}