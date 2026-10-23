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
        view.onRegister(e -> register());
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

    private void register() {
        LoginView.RegistrationDetails details = view.promptRegistration();
        if (details == null) {
            return;
        }

        String fullName = details.getFullName();
        String username = details.getUsername();
        String password = details.getPassword();
        String role = details.getRole();
        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            view.showError("Invalid registration", "Please enter your full name, username, and password.");
            return;
        }
        if (fullName.length() > 100 || username.length() > 50 || password.length() > 100) {
            view.showError("Invalid registration",
                    "Full name must be at most 100 characters, username at most 50, "
                            + "and password at most 100.");
            return;
        }

        try {
            userDAO.register(username, password, fullName, role);
            view.setMessage("Account created. You can now log in.");
        } catch (SQLException ex) {
            if (ex.getErrorCode() == 1062) {
                view.showError("Registration failed", "That username is already in use.");
            } else {
                view.showError("Registration failed", "Database request failed.\n" + ex.getMessage());
            }
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
