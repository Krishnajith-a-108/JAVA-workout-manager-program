import controller.LoginController;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import view.LoginView;

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
