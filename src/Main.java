import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controller.LoginController;
import view.LoginView;

/**
 * The whole program starts here.
 * Notice how short it is: it only connects a View with a Controller.
 * Everything else happens inside the controller / model / view packages.
 */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Swing windows must be created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> new LoginController(new LoginView()).show());
    }
}
