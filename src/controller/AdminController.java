package controller;

import java.sql.SQLException;

import dao.ActivityDAO;
import dao.ProgressDAO;
import dao.UserDAO;
import view.AdminView;
import view.LoginView;

public class AdminController {

    private final AdminView view;

    private final UserDAO userDAO = new UserDAO();
    private final ProgressDAO progressDAO = new ProgressDAO();
    private final ActivityDAO activityDAO = new ActivityDAO();

    public AdminController(AdminView view) {
        this.view = view;
        view.onRefresh(e -> refresh());
        view.onLogout(e -> logout());
        view.onRemoveClient(e -> removeClient());
        view.onRemoveTrainer(e -> removeTrainer());
    }

    public void start() {
        refresh();
        view.setVisible(true);
    }

    private void logout() {
        view.dispose();
        new LoginController(new LoginView()).show();
    }

    /** Reloads all admin tables. */
    private void refresh() {
        loadClientDetails();
        loadClientProgress();
        loadTrainers();
        loadActivities();
    }

    private void loadClientDetails() {
        try {
            view.setClientDetails(userDAO.findClientSummaries());
        } catch (SQLException ex) {
            view.showError("Cannot load the client details.\n" + ex.getMessage());
        }
    }

    private void removeClient() {
        int clientId = view.getSelectedClientId();
        if (clientId < 0) {
            view.showMessage("Select a client to remove.");
            return;
        }
        removeUser(clientId, "CLIENT", "client");
    }

    private void removeTrainer() {
        int trainerId = view.getSelectedTrainerId();
        if (trainerId < 0) {
            view.showMessage("Select a trainer to remove.");
            return;
        }
        removeUser(trainerId, "TRAINER", "trainer");
    }

    private void removeUser(int userId, String role, String displayRole) {
        String name = role.equals("CLIENT")
                ? view.getClientName(userId) : view.getTrainerName(userId);
        if (name == null) {
            view.showError("The selected " + displayRole + " is no longer available. Refresh and try again.");
            refresh();
            return;
        }
        if (!view.confirmRemoval(displayRole, name)) {
            return;
        }

        try {
            if (userDAO.deleteByIdAndRole(userId, role) == 0) {
                view.showError("The selected " + displayRole + " could not be found. Refresh and try again.");
            } else {
                view.showMessage("The " + displayRole + " and associated data were removed.");
            }
            refresh();
        } catch (SQLException ex) {
            view.showError("Cannot remove the " + displayRole + ".\n" + ex.getMessage());
        }
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
