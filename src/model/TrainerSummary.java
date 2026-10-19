package model;

/**
 * MODEL: a small helper object for the admin screen.
 * It is the result of one query: a trainer + how many workouts he assigned.
 */
public class TrainerSummary {

    private final String trainerName;
    private final String username;
    private final int workoutCount;

    public TrainerSummary(String trainerName, String username, int workoutCount) {
        this.trainerName = trainerName;
        this.username = username;
        this.workoutCount = workoutCount;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getUsername() {
        return username;
    }

    public int getWorkoutCount() {
        return workoutCount;
    }
}
