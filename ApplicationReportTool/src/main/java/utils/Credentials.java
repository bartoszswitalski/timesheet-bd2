package utils;

public class Credentials {
    private String login;
    private String password;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setLogin(String login)          { this.login = login; }
    public void setPassword(String password)    { this.password = password; }

    public String getLogin()                    { return this.login; }
    public String getPassword()                 { return this.password; }
}
