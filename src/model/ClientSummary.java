package model;

/** MODEL: client profile details and related record counts for the admin screen. */
public class ClientSummary {

    private final int clientId;
    private final String fullName;
    private final String username;
    private final String trainerNames;
    private final int workoutCount;
    private final int progressCount;

    public ClientSummary(int clientId, String fullName, String username, String trainerNames,
                         int workoutCount, int progressCount) {
        this.clientId = clientId;
        this.fullName = fullName;
        this.username = username;
        this.trainerNames = trainerNames;
        this.workoutCount = workoutCount;
        this.progressCount = progressCount;
    }

    public int getClientId() {
        return clientId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getTrainerNames() {
        return trainerNames;
    }

    public int getWorkoutCount() {
        return workoutCount;
    }

    public int getProgressCount() {
        return progressCount;
    }
}
