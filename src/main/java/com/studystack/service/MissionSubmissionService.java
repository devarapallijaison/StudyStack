package com.studystack.service;

import com.studystack.dao.SubmissionDAO;
import com.studystack.model.MissionSubmission;

import java.sql.SQLException;

public class MissionSubmissionService {
    private final SubmissionDAO submissionDAO;

    public MissionSubmissionService() {
        this(new SubmissionDAO());
    }

    public MissionSubmissionService(SubmissionDAO submissionDAO) {
        this.submissionDAO = submissionDAO;
    }

    public MissionSubmission submit(int userId, int missionId, String answer)
            throws SQLException, SubmissionDAO.DuplicateAttemptException {
        if (answer == null || !answer.matches("[A-D]")) {
            throw new IllegalArgumentException("Choose one of the available answers.");
        }
        return submissionDAO.submitAnswerAndAwardXp(userId, missionId, answer);
    }
}
