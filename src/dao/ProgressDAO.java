package dao;

import db.DBConnection;
import model.Progress;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** MODEL LAYER (data access) for the progress table. */
public class ProgressDAO {

    private static final String BASE_SELECT =
            "SELECT p.progress_id, p.client_id, p.log_date, p.weight_kg, p.status, p.note, "
          + "       u.full_name AS client_name "
          + "FROM progress p "
          + "JOIN users u ON p.client_id = u.user_id ";

    /** Adds one progress entry written by a client. */
    public void add(Progress p) throws SQLException {
        String sql = "INSERT INTO progress (client_id, log_date, weight_kg, status, note) "
                   + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getClientId());
            ps.setDate(2, Date.valueOf(p.getLogDate()));
            ps.setDouble(3, p.getWeightKg());
            ps.setString(4, p.getStatus());
            ps.setString(5, p.getNote());
            ps.executeUpdate();
        }
    }

    /** Deletes every saved progress entry belonging to one client. */
    public int deleteByClient(int clientId) throws SQLException {
        String sql = "DELETE FROM progress WHERE client_id = ?";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, clientId);
            return ps.executeUpdate();
        }
    }

    /** Progress of one client. */
    public List<Progress> findByClient(int clientId) throws SQLException {
        return query(BASE_SELECT + "WHERE p.client_id = ? ORDER BY p.log_date DESC", clientId);
    }

    /** Progress of every client - what the admin looks at. */
    public List<Progress> findAll() throws SQLException {
        return query(BASE_SELECT + "ORDER BY p.log_date DESC", 0);
    }

    private List<Progress> query(String sql, int id) throws SQLException {
        List<Progress> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (sql.contains("?")) {          // findAll() has no parameter
                ps.setInt(1, id);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Progress(
                            rs.getInt("progress_id"),
                            rs.getInt("client_id"),
                            rs.getString("client_name"),
                            rs.getDate("log_date").toLocalDate(),
                            rs.getDouble("weight_kg"),
                            rs.getString("status"),
                            rs.getString("note")));
                }
            }
        }
        return list;
    }
}
