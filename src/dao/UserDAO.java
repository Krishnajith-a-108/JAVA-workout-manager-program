package dao;

import db.DBConnection;
import model.ClientSummary;
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

    /** Creates a self-registered client or trainer account. */
    public void register(String username, String password, String fullName, String role)
            throws SQLException {
        if (!"CLIENT".equals(role) && !"TRAINER".equals(role)) {
            throw new IllegalArgumentException("Only client and trainer accounts can be registered.");
        }

        String sql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, fullName);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }

    /** Removes a client or trainer and all related records via the database foreign keys. */
    public int deleteByIdAndRole(int userId, String role) throws SQLException {
        if (!"CLIENT".equals(role) && !"TRAINER".equals(role)) {
            throw new IllegalArgumentException("Only client and trainer accounts can be removed.");
        }

        String sql = "DELETE FROM users WHERE user_id = ? AND role = ?";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, role);
            return ps.executeUpdate();
        }
    }

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
        String sql = "SELECT u.user_id, u.full_name, u.username, "
                   + "(SELECT COUNT(*) FROM workouts w WHERE w.trainer_id = u.user_id) AS total "
                   + "FROM users u WHERE u.role = 'TRAINER' ORDER BY u.full_name";

        List<TrainerSummary> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new TrainerSummary(rs.getInt("user_id"), rs.getString("full_name"),
                        rs.getString("username"), rs.getInt("total")));
            }
        }
        return list;
    }

    /** Client profile details and related counts for the admin screen. */
    public List<ClientSummary> findClientSummaries() throws SQLException {
        String sql = "SELECT u.user_id, u.full_name, u.username, "
                   + "COALESCE(t.trainer_names, 'Not assigned') AS trainer_names, "
                   + "COALESCE(w.workout_count, 0) AS workout_count, "
                   + "COALESCE(p.progress_count, 0) AS progress_count "
                   + "FROM users u "
                   + "LEFT JOIN ("
                   + "  SELECT w.client_id, "
                   + "         GROUP_CONCAT(DISTINCT t.full_name ORDER BY t.full_name SEPARATOR ', ') "
                   + "             AS trainer_names "
                   + "  FROM workouts w JOIN users t ON w.trainer_id = t.user_id "
                   + "  GROUP BY w.client_id"
                   + ") t ON t.client_id = u.user_id "
                   + "LEFT JOIN ("
                   + "  SELECT client_id, COUNT(*) AS workout_count "
                   + "  FROM workouts GROUP BY client_id"
                   + ") w ON w.client_id = u.user_id "
                   + "LEFT JOIN ("
                   + "  SELECT client_id, COUNT(*) AS progress_count "
                   + "  FROM progress GROUP BY client_id"
                   + ") p ON p.client_id = u.user_id "
                   + "WHERE u.role = 'CLIENT' ORDER BY u.full_name";

        List<ClientSummary> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ClientSummary(
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("trainer_names"),
                        rs.getInt("workout_count"),
                        rs.getInt("progress_count")));
            }
        }
        return list;
    }
}
