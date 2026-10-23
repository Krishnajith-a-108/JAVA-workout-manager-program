package view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
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
    private final JButton registerButton = new JButton("Create account");
    private final JLabel message = new JLabel(" ");

    public static class RegistrationDetails {
        private final String fullName;
        private final String username;
        private final String password;
        private final String role;

        public RegistrationDetails(String fullName, String username, String password, String role) {
            this.fullName = fullName;
            this.username = username;
            this.password = password;
            this.role = role;
        }

        public String getFullName() { return fullName; }
        public String getUsername() { return username; }
        public String getPassword() { return password; }
        public String getRole() { return role; }
    }

    public LoginView() {
        super("Gym Workout Manager - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 270);
        setLocationRelativeTo(null);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);

        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Username:"), c);
        c.gridx = 1;              form.add(usernameField, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Password:"), c);
        c.gridx = 1;              form.add(passwordField, c);
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2; form.add(loginButton, c);
        c.gridy = 3;              form.add(registerButton, c);
        c.gridy = 4;              form.add(message, c);

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

    public void onRegister(ActionListener listener) {
        registerButton.addActionListener(listener);
    }

    public RegistrationDetails promptRegistration() {
        JTextField fullName = new JTextField(18);
        JTextField username = new JTextField(18);
        JPasswordField password = new JPasswordField(18);
        JComboBox<String> role = new JComboBox<>(new String[]{"CLIENT", "TRAINER"});

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0; c.gridy = 0; form.add(new JLabel("Full name:"), c);
        c.gridx = 1; form.add(fullName, c);
        c.gridx = 0; c.gridy = 1; form.add(new JLabel("Username:"), c);
        c.gridx = 1; form.add(username, c);
        c.gridx = 0; c.gridy = 2; form.add(new JLabel("Password:"), c);
        c.gridx = 1; form.add(password, c);
        c.gridx = 0; c.gridy = 3; form.add(new JLabel("Register as:"), c);
        c.gridx = 1; form.add(role, c);

        int result = JOptionPane.showConfirmDialog(this, form, "Create account",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return null;
        }
        return new RegistrationDetails(fullName.getText().trim(), username.getText().trim(),
                new String(password.getPassword()), (String) role.getSelectedItem());
    }
}
