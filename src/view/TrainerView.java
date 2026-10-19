package view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.User;
import model.Workout;

/**
 * VIEW: the trainer window.
 * Tab 1 -> form to assign a workout to a client.
 * Tab 2 -> the workouts this trainer assigned.
 */
public class TrainerView extends JFrame {

    private final JComboBox<User> clientBox = new JComboBox<>();
    private final JTextField exerciseField = new JTextField(16);
    private final JTextField setsField = new JTextField(4);
    private final JTextField repsField = new JTextField(4);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField remarkField = new JTextField(28);

    private final JButton assignButton = new JButton("Assign Workout");
    private final JButton refreshButton = new JButton("Refresh");
    private final JButton logoutButton = new JButton("Logout");

    private final DefaultTableModel myWorkouts =
            new DefaultTableModel(new Object[]{"Date", "Client", "Exercise", "Sets", "Reps", "Remark"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    public TrainerView(String trainerName) {
        super("Trainer - " + trainerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Assign Workout", buildAssignTab());
        tabs.addTab("My Assigned Workouts", buildListTab());

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("  Logged in as TRAINER: " + trainerName), BorderLayout.WEST);
        JPanel headerActions = new JPanel();
        headerActions.add(logoutButton);
        header.add(headerActions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildAssignTab() {
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;

        int row = 0;
        addRow(form, c, row++, "Client:", clientBox);
        addRow(form, c, row++, "Exercise:", exerciseField);
        addRow(form, c, row++, "Sets:", setsField);
        addRow(form, c, row++, "Reps:", repsField);
        addRow(form, c, row++, "Date (yyyy-MM-dd):", dateField);
        addRow(form, c, row++, "Remark:", remarkField);

        c.gridx = 1; c.gridy = row;
        form.add(assignButton, c);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(form, BorderLayout.NORTH);
        return wrapper;
    }

    private JPanel buildListTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(new JTable(myWorkouts)), BorderLayout.CENTER);

        JPanel south = new JPanel();
        south.add(refreshButton);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void addRow(JPanel form, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel(label), c);
        c.gridx = 1;
        form.add(field, c);
    }

    // ---------- the controller fills the screen ----------
    public void setClients(List<User> clients) {
        clientBox.removeAllItems();
        for (User u : clients) {
            clientBox.addItem(u);        // User.toString() shows the full name
        }
    }

    public void setWorkouts(List<Workout> workouts) {
        myWorkouts.setRowCount(0);
        for (Workout w : workouts) {
            myWorkouts.addRow(new Object[]{
                    w.getWorkoutDate(), w.getClientName(), w.getExercise(),
                    w.getSets(), w.getReps(), w.getRemark()});
        }
    }

    // ---------- raw input for the controller ----------
    public User getSelectedClient() {
        return (User) clientBox.getSelectedItem();
    }

    public String getExerciseText() {
        return exerciseField.getText().trim();
    }

    public String getSetsText() {
        return setsField.getText().trim();
    }

    public String getRepsText() {
        return repsField.getText().trim();
    }

    public String getDateText() {
        return dateField.getText().trim();
    }

    public String getRemarkText() {
        return remarkField.getText().trim();
    }

    public void clearAssignForm() {
        exerciseField.setText("");
        setsField.setText("");
        repsField.setText("");
        remarkField.setText("");
    }

    // ---------- controller hooks ----------
    public void onAssign(ActionListener listener) {
        assignButton.addActionListener(listener);
    }

    public void onRefresh(ActionListener listener) {
        refreshButton.addActionListener(listener);
    }

    public void onLogout(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
