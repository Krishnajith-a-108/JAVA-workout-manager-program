package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import dao.ActivityDAO;
import dao.UserDAO;
import dao.WorkoutDAO;
import model.User;
import model.Workout;
import view.LoginView;
import view.TrainerView;

/**
 * CONTROLLER for the trainer window.
 * Assigning a workout = 1 row in workouts + 1 row in activities
 * (that second row is what the admin reads later).
 */
public class TrainerController {

    private final TrainerView view;
    private final int trainerId;

    private final UserDAO userDAO = new UserDAO();
    private final WorkoutDAO workoutDAO = new WorkoutDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();

    public TrainerController(TrainerView view, int trainerId) {
        this.view = view;
        this.trainerId = trainerId;
        view.onAssign(e -> assignWorkout());
        view.onRefresh(e -> loadWorkouts());
        view.onLogout(e -> logout());
    }

    public void start() {
        loadClients();
        loadWorkouts();
        view.setVisible(true);
    }

    private void logout() {
        view.dispose();
        new LoginController(new LoginView()).show();
    }

    private void loadClients() {
        try {
            view.setClients(userDAO.findByRole("CLIENT"));
        } catch (SQLException ex) {
            view.showError("Cannot load the client list.\n" + ex.getMessage());
        }
    }

    private void loadWorkouts() {
        try {
            view.setWorkouts(workoutDAO.findByTrainer(trainerId));
        } catch (SQLException ex) {
            view.showError("Cannot load the workouts.\n" + ex.getMessage());
        }
    }

    private void assignWorkout() {
        User client = view.getSelectedClient();

        if (client == null) {
            view.showError("There is no client in the database.");
            return;
        }
        if (view.getExerciseText().isEmpty()) {
            view.showError("Please type the exercise name.");
            return;
        }

        int sets;
        int reps;
        LocalDate date;

        try {
            sets = Integer.parseInt(view.getSetsText());
            reps = Integer.parseInt(view.getRepsText());
        } catch (NumberFormatException ex) {
            view.showError("Sets and reps must be numbers.");
            return;
        }

        try {
            date = LocalDate.parse(view.getDateText());
        } catch (DateTimeParseException ex) {
            view.showError("Date must look like 2026-10-19");
            return;
        }

        Workout workout = new Workout(client.getUserId(), trainerId,
                view.getExerciseText(), sets, reps, date, view.getRemarkText());

        try {
            workoutDAO.assign(workout);

            // let the admin know what this trainer did
            activityDAO.log(trainerId, "Assigned workout '" + workout.getExercise()
                    + "' to client " + client.getFullName());

            view.clearAssignForm();
            loadWorkouts();
            view.showMessage("Workout assigned to " + client.getFullName());
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
        }
    }
}
