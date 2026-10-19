package dao;

import db.DBConnection;
import model.Activity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** MODEL LAYER (data access) for the activities table. */
public class ActivityDAO {

    /** Records one action of a user (a trainer assigned a workout, for example). */
    public void log(int userId, String action) throws SQLException {
        String sql = "INSERT INTO activities (user_id, action) VALUES (?, ?)";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.executeUpdate();
        }
    }

    /** Activities of the trainers only - the list the admin reads. */
    public List<Activity> findTrainerActivities() throws SQLException {
        String sql = "SELECT a.activity_id, a.user_id, a.action, a.log_time, u.full_name "
                   + "FROM activities a "
                   + "JOIN users u ON a.user_id = u.user_id "
                   + "WHERE u.role = 'TRAINER' "
                   + "ORDER BY a.log_time DESC";

        List<Activity> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String time = String.valueOf(rs.getTimestamp("log_time"));
                list.add(new Activity(
                        rs.getInt("activity_id"),
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("action"),
                        time.length() >= 16 ? time.substring(0, 16) : time));
            }
        }
        return list;
    }
}
