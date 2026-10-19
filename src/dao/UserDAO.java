package dao;

import db.DBConnection;
import model.TrainerSummary;
import model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MODEL LAYER (data access).
 * Every SQL statement about the users table lives here, and nowhere else.
 */
public class UserDAO {

    /** @return the matching user, or null when the username/password is wrong. */
    public User login(String username, String password) throws SQLException {
        String sql = "SELECT user_id, username, full_name, role FROM users "
                   + "WHERE username = ? AND password = ?";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("role"));
                }
            }
        }
        return null;      // no row found
    }

    /** All users having one role, for example every CLIENT. */
    public List<User> findByRole(String role) throws SQLException {
        String sql = "SELECT user_id, username, full_name, role FROM users "
                   + "WHERE role = ? ORDER BY full_name";

        List<User> users = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, role);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(new User(rs.getInt("user_id"), rs.getString("username"),
                            rs.getString("full_name"), rs.getString("role")));
                }
            }
        }
        return users;
    }

    /** Every trainer together with the number of workouts he assigned (admin screen). */
    public List<TrainerSummary> findTrainerSummaries() throws SQLException {
        String sql = "SELECT u.full_name, u.username, "
                   + "(SELECT COUNT(*) FROM workouts w WHERE w.trainer_id = u.user_id) AS total "
                   + "FROM users u WHERE u.role = 'TRAINER' ORDER BY u.full_name";

        List<TrainerSummary> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TrainerSummary(rs.getString("full_name"),
                        rs.getString("username"), rs.getInt("total")));
            }
        }
        return list;
    }
}
