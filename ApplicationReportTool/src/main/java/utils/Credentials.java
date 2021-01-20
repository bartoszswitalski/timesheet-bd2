package utils;

public class Credentials {
    private String login;
    private String password;
    private String departmentId;
    private String role;

    public Credentials(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLogin() {
        return this.login;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDepartment_id() {
        return this.departmentId;
    }

    public String getRole() {
        return this.role;
    }
}
