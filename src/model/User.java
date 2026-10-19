package model;

/** MODEL: one row of the users table. Plain data, no Swing, no SQL. */
public class User {

    private final int userId;
    private final String username;
    private final String fullName;
    private final String role;        // CLIENT / TRAINER / ADMIN

    public User(int userId, String username, String fullName, String role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    /** So a JComboBox/list shows the person's name instead of a memory address. */
    @Override
    public String toString() {
        return fullName;
    }
}
