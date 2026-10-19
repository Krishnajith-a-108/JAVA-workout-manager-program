package controller;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import dao.ProgressDAO;
import dao.WorkoutDAO;
import model.Progress;
import view.LoginView;
import view.ClientView;

/**
 * CONTROLLER for the client window.
 * It loads the client's workouts and progress, validates the small form
 * and saves a new progress row.
 */
public class ClientController {

    private final ClientView view;
    private final int clientId;

    private final WorkoutDAO workoutDAO = new WorkoutDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();

    public ClientController(ClientView view, int clientId) {
        this.view = view;
        this.clientId = clientId;
        view.onSaveProgress(e -> saveProgress());
        view.onClearProgress(e -> view.clearProgressForm());
        view.onClearAllProgress(e -> clearAllProgress());
        view.onLogout(e -> logout());
    }

    /** Loads the data and shows the window. */
    public void start() {
        loadWorkouts();
        loadProgress();
        view.setVisible(true);
    }

    private void logout() {
        view.dispose();
        new LoginController(new LoginView()).show();
    }

    private void clearAllProgress() {
        if (!view.confirmClearAllProgress()) {
            return;
        }

        try {
            int deletedCount = progressDAO.deleteByClient(clientId);
            loadProgress();
            view.showMessage(deletedCount == 0
                    ? "There were no saved progress entries to clear."
                    : "Cleared " + deletedCount + " saved progress entr"
                            + (deletedCount == 1 ? "y." : "ies."));
        } catch (SQLException ex) {
            view.showError("Could not clear saved progress.\n" + ex.getMessage());
        }
    }

    private void loadWorkouts() {
        try {
            view.setWorkouts(workoutDAO.findByClient(clientId));
        } catch (SQLException ex) {
            view.showError("Cannot load the workouts.\n" + ex.getMessage());
        }
    }

    private void loadProgress() {
        try {
            view.setProgress(progressDAO.findByClient(clientId));
        } catch (SQLException ex) {
            view.showError("Cannot load the progress.\n" + ex.getMessage());
        }
    }

    /** Validates the form, then saves one row in the progress table. */
    private void saveProgress() {
        LocalDate date;
        double weight;

        try {
            date = LocalDate.parse(view.getDateText());
        } catch (DateTimeParseException ex) {
            view.showError("Date must look like 2026-10-19");
            return;
        }

        try {
            weight = Double.parseDouble(view.getWeightText());
        } catch (NumberFormatException ex) {
            view.showError("Weight must be a number, for example 75.5");
            return;
        }

        Progress progress = new Progress(clientId, date, weight, view.getStatus(), view.getNote());

        try {
            progressDAO.add(progress);
            view.clearProgressForm();
            loadProgress();
            view.showMessage("Progress saved.");
        } catch (SQLException ex) {
            view.showError("Database error: " + ex.getMessage());
        }
    }
}
