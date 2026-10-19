package controller;

import java.sql.SQLException;

import dao.UserDAO;
import model.User;
import view.AdminView;
import view.ClientView;
import view.LoginView;
import view.TrainerView;

/**
 * CONTROLLER for the login screen.
 * It reads what the user typed, asks the model (UserDAO) and then
 * decides which window to open. The view itself knows nothing about this.
 */
public class LoginController {

    private final LoginView view;
    private final UserDAO userDAO = new UserDAO();

    public LoginController(LoginView view) {
        this.view = view;
        view.onLogin(e -> login());
    }

    /** Shows the login window. */
    public void show() {
        view.setVisible(true);
    }

    private void login() {
        String username = view.getUsername();
        String password = view.getPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.setMessage("Please enter username and password");
            return;
        }

        try {
            User user = userDAO.login(username, password);

            if (user == null) {
                view.setMessage("Wrong username or password");
                return;
            }

            view.dispose();                       // close the login window
            openDashboardFor(user);

        } catch (SQLException ex) {
            String message = "Database request failed.\n" + ex.getMessage();
            if (ex.getErrorCode() == 1045 || "28000".equals(ex.getSQLState())) {
                message += "\n\nMySQL rejected the database username or password. "
                        + "Run run.bat and enter your MySQL credentials.";
            } else if (ex.getErrorCode() == 1049) {
                message += "\n\nThe gym_db database does not exist. Import gym_db.sql "
                        + "using MySQL Workbench (note: the script drops and recreates gym_db).";
            } else if ("08S01".equals(ex.getSQLState())) {
                message += "\n\nMySQL could not be reached. Make sure the MySQL service is running "
                        + "and the database URL and port are correct.";
            }
            view.showError("Database error", message);
        }
    }

    /** The role stored in the database decides which dashboard opens. */
    private void openDashboardFor(User user) {
        String name = user.getFullName();

        if (user.getRole().equals("CLIENT")) {
            new ClientController(new ClientView(name), user.getUserId()).start();
        } else if (user.getRole().equals("TRAINER")) {
            new TrainerController(new TrainerView(name), user.getUserId()).start();
        } else {
            new AdminController(new AdminView(name)).start();
        }
    }
}
