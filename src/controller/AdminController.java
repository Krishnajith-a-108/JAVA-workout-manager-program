package controller;

import java.sql.SQLException;

import dao.ActivityDAO;
import dao.ProgressDAO;
import dao.UserDAO;
import view.AdminView;
import view.LoginView;

/
public class AdminController {

    private final AdminView view;

    private final UserDAO userDAO = new UserDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();

    public AdminController(AdminView view) {
        this.view = view;
        view.onRefresh(e -> refresh());
        view.onLogout(e -> logout());
    }

    public void start() {
        refresh();
        view.setVisible(true);
    }

    private void logout() {
        view.dispose();
        new LoginController(new LoginView()).show();
    }

    /** Reloads all three tables. */
    private void refresh() {
        loadClientProgress();
        loadTrainers();
        loadActivities();
    }

    private void loadClientProgress() {
        try {
            view.setClientProgress(progressDAO.findAll());
        } catch (SQLException ex) {
            view.showError("Cannot load the client progress.\n" + ex.getMessage());
        }
    }

    private void loadTrainers() {
        try {
            view.setTrainers(userDAO.findTrainerSummaries());
        } catch (SQLException ex) {
            view.showError("Cannot load the trainers.\n" + ex.getMessage());
        }
    }

    private void loadActivities() {
        try {
            view.setActivities(activityDAO.findTrainerActivities());
        } catch (SQLException ex) {
            view.showError("Cannot load the activities.\n" + ex.getMessage());
        }
    }
}
