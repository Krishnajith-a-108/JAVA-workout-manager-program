package model;

/**
 * MODEL: a small helper object for the admin screen.
 * It is the result of one query: a trainer + how many workouts he assigned.
 */
public class TrainerSummary {

    private final int trainerId;
    private final String trainerName;
    private final String username;
    private final int workoutCount;

    public TrainerSummary(int trainerId, String trainerName, String username, int workoutCount) {
        this.trainerId = trainerId;
        this.trainerName = trainerName;
        this.username = username;
        this.workoutCount = workoutCount;
    }

    public int getTrainerId() {
        return trainerId;
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
