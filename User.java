package znulib;

// کلاس پایه کاربر
public abstract class User {
    protected String username;
    protected String password;
    protected boolean active;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.active = true;
    }

    // 1-2 ورود به سیستم
    public boolean login(String username, String password) {
        return this.username.equals(username) && this.password.equals(password) && active;
    }

    // 3-2 امکان تغییر رمز عبور
    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    // Getter and Setter methods
    public String getUsername() { return username; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
