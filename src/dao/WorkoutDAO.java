package dao;

import db.DBConnection;
import model.Workout;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** MODEL LAYER (data access) for the workouts table. */
public class WorkoutDAO {

    /** The joins give us the client name and the trainer name as well. */
    private static final String BASE_SELECT =
            "SELECT w.workout_id, w.client_id, w.trainer_id, w.exercise, w.sets, w.reps, "
          + "       w.workout_date, w.remark, "
          + "       c.full_name AS client_name, t.full_name AS trainer_name "
          + "FROM workouts w "
          + "JOIN users c ON w.client_id  = c.user_id "
          + "JOIN users t ON w.trainer_id = t.user_id ";

    /** Saves a workout that a trainer assigned to a client. */
    public void assign(Workout w) throws SQLException {
        String sql = "INSERT INTO workouts "
                   + "(client_id, trainer_id, exercise, sets, reps, workout_date, remark) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, w.getClientId());
            ps.setInt(2, w.getTrainerId());
            ps.setString(3, w.getExercise());
            ps.setInt(4, w.getSets());
            ps.setInt(5, w.getReps());
            ps.setDate(6, Date.valueOf(w.getWorkoutDate()));   // LocalDate -> SQL DATE
            ps.setString(7, w.getRemark());
            ps.executeUpdate();
        }
    }

    /** Workouts of one client (client screen). */
    public List<Workout> findByClient(int clientId) throws SQLException {
        return query(BASE_SELECT + "WHERE w.client_id = ? ORDER BY w.workout_date DESC", clientId);
    }

    /** Workouts assigned by one trainer (trainer screen). */
    public List<Workout> findByTrainer(int trainerId) throws SQLException {
        return query(BASE_SELECT + "WHERE w.trainer_id = ? ORDER BY w.workout_date DESC", trainerId);
    }

    /** Keeps the two select methods short - they differ only in the WHERE clause. */
    private List<Workout> query(String sql, int id) throws SQLException {
        List<Workout> list = new ArrayList<>();

        try (Connection con = DBConnection.get();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Workout(
                            rs.getInt("workout_id"),
                            rs.getInt("client_id"),
                            rs.getInt("trainer_id"),
                            rs.getString("client_name"),
                            rs.getString("trainer_name"),
                            rs.getString("exercise"),
                            rs.getInt("sets"),
                            rs.getInt("reps"),
                            rs.getDate("workout_date").toLocalDate(),
                            rs.getString("remark")));
                }
            }
        }
        return list;
    }
}
