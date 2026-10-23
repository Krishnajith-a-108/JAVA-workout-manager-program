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
import javax.swing.JTable;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;

import model.Activity;
import model.ClientSummary;
import model.Progress;
import model.TrainerSummary;


public class AdminView extends JFrame {

    private final DefaultTableModel progressTable =
            new DefaultTableModel(new Object[]{"Client", "Date", "Weight (kg)", "Status", "Note"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel clientTable =
            new DefaultTableModel(
                    new Object[]{"Client ID", "Full Name", "Username", "Trainer(s)",
                            "Assigned Workouts", "Progress Entries"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel trainerTable =
            new DefaultTableModel(new Object[]{"Trainer ID", "Trainer", "Username", "Workouts Assigned"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    private final DefaultTableModel activityTable =
            new DefaultTableModel(new Object[]{"Trainer", "Activity", "Time"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };

    // every tab gets its own Refresh button, the controller listens to all of them
    private final List<JButton> refreshButtons = new ArrayList<>();
    private final JButton logoutButton = new JButton("Logout");
    private final JButton removeClientButton = new JButton("Remove selected client");
    private final JButton removeTrainerButton = new JButton("Remove selected trainer");
    private final JTable clientJTable = new JTable(clientTable);
    private final JTable trainerJTable = new JTable(trainerTable);

    public AdminView(String adminName) {
        super("Admin - " + adminName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 560);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Client Details", withRefresh(new JScrollPane(clientJTable), removeClientButton));
        tabs.addTab("Client Progress", withRefresh(new JScrollPane(new JTable(progressTable))));
        tabs.addTab("Trainers", withRefresh(new JScrollPane(trainerJTable), removeTrainerButton));
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
        return withRefresh(table, null);
    }

    private JPanel withRefresh(JScrollPane table, JButton removeButton) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(table, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        if (removeButton != null) {
            south.add(removeButton);
        }
        JButton button = new JButton("Refresh");
        refreshButtons.add(button);
        south.add(button);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ---------- the controller fills the tables ----------
    public void setClientDetails(List<ClientSummary> list) {
        clientTable.setRowCount(0);
        for (ClientSummary client : list) {
            clientTable.addRow(new Object[]{
                    client.getClientId(), client.getFullName(), client.getUsername(),
                    client.getTrainerNames(), client.getWorkoutCount(), client.getProgressCount()});
        }
    }

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
                    t.getTrainerId(), t.getTrainerName(), t.getUsername(), t.getWorkoutCount()});
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

    public void onRemoveClient(ActionListener listener) {
        removeClientButton.addActionListener(listener);
    }

    public void onRemoveTrainer(ActionListener listener) {
        removeTrainerButton.addActionListener(listener);
    }

    public int getSelectedClientId() {
        int selectedRow = clientJTable.getSelectedRow();
        return selectedRow < 0 ? -1 : (int) clientTable.getValueAt(
                clientJTable.convertRowIndexToModel(selectedRow), 0);
    }

    public int getSelectedTrainerId() {
        int selectedRow = trainerJTable.getSelectedRow();
        return selectedRow < 0 ? -1 : (int) trainerTable.getValueAt(
                trainerJTable.convertRowIndexToModel(selectedRow), 0);
    }

    public String getClientName(int clientId) {
        for (int row = 0; row < clientTable.getRowCount(); row++) {
            if ((int) clientTable.getValueAt(row, 0) == clientId) {
                return (String) clientTable.getValueAt(row, 1);
            }
        }
        return null;
    }

    public String getTrainerName(int trainerId) {
        for (int row = 0; row < trainerTable.getRowCount(); row++) {
            if ((int) trainerTable.getValueAt(row, 0) == trainerId) {
                return (String) trainerTable.getValueAt(row, 1);
            }
        }
        return null;
    }

    public boolean confirmRemoval(String role, String name) {
        String message = "Permanently remove " + role.toLowerCase() + " \"" + name
                + "\" and all associated workouts, progress, and activity data?";
        return JOptionPane.showConfirmDialog(this, message, "Confirm removal",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public void showMessage(String text) {
        JOptionPane.showMessageDialog(this, text, "Admin", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String text) {
        JOptionPane.showMessageDialog(this, text, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
