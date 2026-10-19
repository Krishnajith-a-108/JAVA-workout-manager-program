package view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import model.Activity;
import model.Progress;
import model.TrainerSummary;


public class AdminView extends JFrame {

    private final DefaultTableModel progressTable =
            new DefaultTableModel(new Object[]{"Client", "Date", "Weight (kg)", "Status", "Note"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel trainerTable =
            new DefaultTableModel(new Object[]{"Trainer", "Username", "Workouts Assigned"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel activityTable =
            new DefaultTableModel(new Object[]{"Trainer", "Activity", "Time"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    // every tab gets its own Refresh button, the controller listens to all of them
    private final List<JButton> refreshButtons = new ArrayList<>();
    private final JButton logoutButton = new JButton("Logout");

    public AdminView(String adminName) {
        super("Admin - " + adminName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(920, 520);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Client Progress", withRefresh(new JScrollPane(new JTable(progressTable))));
        tabs.addTab("Trainers", withRefresh(new JScrollPane(new JTable(trainerTable))));
        tabs.addTab("Trainer Activities", withRefresh(new JScrollPane(new JTable(activityTable))));

        JPanel header = new JPanel(new BorderLayout());
        header.add(new JLabel("  Logged in as ADMIN: " + adminName), BorderLayout.WEST);
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        headerActions.add(logoutButton);
        header.add(headerActions, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel withRefresh(JScrollPane table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(table, BorderLayout.CENTER);

        JButton button = new JButton("Refresh");
        refreshButtons.add(button);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(button);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- the controller fills the three tables ----------
    public void setClientProgress(List<Progress> list) {
        progressTable.setRowCount(0);
        for (Progress p : list) {
            progressTable.addRow(new Object[]{
                    p.getClientName(), p.getLogDate(), p.getWeightKg(), p.getStatus(), p.getNote()});
        }
    }

    public void setTrainers(List<TrainerSummary> list) {
        trainerTable.setRowCount(0);
        for (TrainerSummary t : list) {
            trainerTable.addRow(new Object[]{
                    t.getTrainerName(), t.getUsername(), t.getWorkoutCount()});
        }
    }

    public void setActivities(List<Activity> list) {
        activityTable.setRowCount(0);
        for (Activity a : list) {
            activityTable.addRow(new Object[]{
                    a.getUserName(), a.getAction(), a.getLogTime()});
        }
    }

    // ---------- controller hooks ----------
    public void onRefresh(ActionListener listener) {
        for (JButton button : refreshButtons) {
            button.addActionListener(listener);
        }
    }

    public void onLogout(ActionListener listener) {
        logoutButton.addActionListener(listener);
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
