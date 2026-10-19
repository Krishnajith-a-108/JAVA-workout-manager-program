package model;

/** MODEL: one row of the activities table (what a trainer did). */
public class Activity {

    private final int activityId;
    private final int userId;
    private final String userName;
    private final String action;
    private final String logTime;      // already formatted text, ready to display

    public Activity(int activityId, int userId, String userName, String action, String logTime) {
        this.activityId = activityId;
        this.userId = userId;
        this.userName = userName;
        this.action = action;
        this.logTime = logTime;
    }

    public int getActivityId() {
        return activityId;
    }

    public int getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAction() {
        return action;
    }

    public String getLogTime() {
        return logTime;
    }
}
