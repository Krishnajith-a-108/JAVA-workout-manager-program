package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * VIEW: the login window.
 * It only shows widgets. It does not read the database and it does not
 * decide who may log in - the LoginController does that.
 */
public class LoginView extends JFrame {

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);
    private final JButton loginButton = new JButton("Login");
    private final JLabel message = new JLabel(" ");

    public LoginView() {
        super("Gym Workout Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 240);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Username:"), c);
        c.gridx = 1;              form.add(usernameField, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Password:"), c);
        c.gridx = 1;              form.add(passwordField, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(loginButton, c);
        c.gridy = 3;              form.add(message, c);

        add(form);
        getRootPane().setDefaultButton(loginButton);   // Enter key presses Login
    }

    // ---------- things the controller reads ----------
    public String getUsername() {
        return usernameField.getText().trim();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    // ---------- things the controller changes ----------
    public void setMessage(String text) {
        message.setText(text);
    }

    public void showError(String title, String text) {
        JOptionPane.showMessageDialog(this, text, title, JOptionPane.ERROR_MESSAGE);
    }

    // ---------- the controller plugs itself into the button ----------
    public void onLogin(ActionListener listener) {
        loginButton.addActionListener(listener);
    }
}
