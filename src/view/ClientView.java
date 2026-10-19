package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import model.Progress;
import model.Workout;

/**
 * VIEW: the client window.
 * Tab 1 -> workouts assigned to this client.
 * Tab 2 -> his own progress + a small form.
 * No SQL here: the controller hands over ready-made lists of objects.
 */
public class ClientView extends JFrame {

    private final DefaultTableModel workoutTable =
            new DefaultTableModel(new Object[]{"Date", "Exercise", "Sets", "Reps", "Trainer", "Remark"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel progressTable =
            new DefaultTableModel(new Object[]{"Date", "Weight (kg)", "Status", "Note"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
    private final JTextField weightField = new JTextField(5);
    private final JComboBox<String> statusBox = new JComboBox<>(new String[]{"DONE", "PARTIAL", "SKIPPED"});
    private final JTextField noteField = new JTextField(22);
    private final JButton saveButton = new JButton("Save");
    private final JButton clearProgressButton = new JButton("Clear Form");
    private final JButton clearAllProgressButton = new JButton("Clear All Progress");
    private final JButton logoutButton = new JButton("Logout");

    public ClientView(String clientName) {
        super("Client - " + clientName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 480);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("My Workouts", new JScrollPane(new JTable(workoutTable)));
        tabs.addTab("My Progress", buildProgressTab());

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("  Logged in as CLIENT: " + clientName), BorderLayout.WEST);
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.add(logoutButton);
        header.add(headerActions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildProgressTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(new JTable(progressTable)), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.setBorder(BorderFactory.createTitledBorder("Add my progress"));
        form.add(new JLabel("Date (yyyy-MM-dd):"));
        form.add(dateField);
        form.add(new JLabel("Weight:"));
        form.add(weightField);
        form.add(new JLabel("Status:"));
        form.add(statusBox);
        form.add(new JLabel("Note:"));
        form.add(noteField);
        form.add(saveButton);
        form.add(clearProgressButton);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(form, BorderLayout.CENTER);
        JPanel historyActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        historyActions.add(clearAllProgressButton);
        bottom.add(historyActions, BorderLayout.SOUTH);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- the controller fills the tables ----------
    public void setWorkouts(List<Workout> workouts) {
        workoutTable.setRowCount(0);
        for (Workout w : workouts) {
            workoutTable.addRow(new Object[]{
                    w.getWorkoutDate(), w.getExercise(), w.getSets(),
                    w.getReps(), w.getTrainerName(), w.getRemark()});
        }
    }

    public void setProgress(List<Progress> list) {
        progressTable.setRowCount(0);
        for (Progress p : list) {
            progressTable.addRow(new Object[]{
                    p.getLogDate(), p.getWeightKg(), p.getStatus(), p.getNote()});
        }
    }

    // ---------- raw text the controller will validate ----------
    public String getDateText() {
        return dateField.getText().trim();
    }

    public String getWeightText() {
        return weightField.getText().trim();
    }

    public String getStatus() {
        return (String) statusBox.getSelectedItem();
    }

    public String getNote() {
        return noteField.getText().trim();
    }

    public void clearProgressForm() {
        dateField.setText(LocalDate.now().toString());
        weightField.setText("");
        statusBox.setSelectedIndex(0);
        noteField.setText("");
    }

    // ---------- controller hooks ----------
    public void onSaveProgress(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    public void onClearProgress(ActionListener listener) {
        clearProgressButton.addActionListener(listener);
    }

    public void onClearAllProgress(ActionListener listener) {
        clearAllProgressButton.addActionListener(listener);
    }

    public void onLogout(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public boolean confirmClearAllProgress() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Permanently delete all of your saved progress entries? This cannot be undone.",
                "Clear All Progress",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        return choice == JOptionPane.YES_OPTION;
    }

    public void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text);
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
