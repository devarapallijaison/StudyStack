package com.studystack.model;

import java.time.LocalDateTime;

public class MissionSubmission {
    private long submissionId;
    private int userId;
    private int missionId;
    private String selectedAnswer;
    private boolean correct;
    private int xpEarned;
    private LocalDateTime submittedAt;
    private String missionTitle;

    public long getSubmissionId() { return submissionId; }
    public void setSubmissionId(long submissionId) { this.submissionId = submissionId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getMissionId() { return missionId; }
    public void setMissionId(int missionId) { this.missionId = missionId; }
    public String getSelectedAnswer() { return selectedAnswer; }
    public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }
    public int getXpEarned() { return xpEarned; }
    public void setXpEarned(int xpEarned) { this.xpEarned = xpEarned; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public String getMissionTitle() { return missionTitle; }
    public void setMissionTitle(String missionTitle) { this.missionTitle = missionTitle; }
}
