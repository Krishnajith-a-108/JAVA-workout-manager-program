package model;

import java.time.LocalDate;

/** MODEL: one row of the progress table (the client's own progress log). */
public class Progress {

    private final int progressId;
    private final int clientId;
    private final String clientName;
    private final LocalDate logDate;
    private final double weightKg;
    private final String status;      // DONE / PARTIAL / SKIPPED
    private final String note;

    public Progress(int clientId, LocalDate logDate, double weightKg, String status, String note) {
        this(0, clientId, null, logDate, weightKg, status, note);
    }

    public Progress(int progressId, int clientId, String clientName, LocalDate logDate,
                    double weightKg, String status, String note) {
        this.progressId = progressId;
        this.clientId = clientId;
        this.clientName = clientName;
        this.logDate = logDate;
        this.weightKg = weightKg;
        this.status = status;
        this.note = note;
    }

    public int getProgressId() {
        return progressId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }
}
