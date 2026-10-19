package model;

import java.time.LocalDate;

/**
 * MODEL: one workout of the workouts table.
 * First constructor  -> a brand new workout the trainer wants to save.
 * Second constructor -> a workout that was read from the database
 *                       (it also carries the client and trainer names).
 */
public class Workout {

    private final int workoutId;
    private final int clientId;
    private final int trainerId;
    private final String clientName;
    private final String trainerName;
    private final String exercise;
    private final int sets;
    private final int reps;
    private final LocalDate workoutDate;
    private final String remark;

    public Workout(int clientId, int trainerId, String exercise, int sets, int reps,
                   LocalDate workoutDate, String remark) {
        this(0, clientId, trainerId, null, null, exercise, sets, reps, workoutDate, remark);
    }

    public Workout(int workoutId, int clientId, int trainerId, String clientName, String trainerName,
                   String exercise, int sets, int reps, LocalDate workoutDate, String remark) {
        this.workoutId = workoutId;
        this.clientId = clientId;
        this.trainerId = trainerId;
        this.clientName = clientName;
        this.trainerName = trainerName;
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.workoutDate = workoutDate;
        this.remark = remark;
    }

    public int getWorkoutId() {
        return workoutId;
    }

    public int getClientId() {
        return clientId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public String getClientName() {
        return clientName;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public String getExercise() {
        return exercise;
    }

    public int getSets() {
        return sets;
    }

    public int getReps() {
        return reps;
    }

    public LocalDate getWorkoutDate() {
        return workoutDate;
    }

    public String getRemark() {
        return remark;
    }
}
