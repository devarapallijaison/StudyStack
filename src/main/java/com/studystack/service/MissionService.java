package com.studystack.service;

import com.studystack.dao.MissionDAO;
import com.studystack.model.Mission;

import java.sql.SQLException;
import java.util.Set;

public class MissionService {
    private static final Set<String> CATEGORIES = Set.of("Java", "SQL", "DSA");
    private static final Set<String> DIFFICULTIES = Set.of("Easy", "Medium", "Hard");
    private static final Set<String> ANSWERS = Set.of("A", "B", "C", "D");
    private final MissionDAO missionDAO;

    public MissionService() { this(new MissionDAO()); }
    public MissionService(MissionDAO missionDAO) { this.missionDAO = missionDAO; }

    public int save(Mission mission) throws SQLException, ValidationException {
        require(mission.getMissionId() >= 0, "Invalid mission identifier.");
        require(mission.getTitle() != null && !mission.getTitle().trim().isEmpty() && mission.getTitle().trim().length() <= 150,
                "Enter a mission title of up to 150 characters.");
        require(mission.getDescription() != null && !mission.getDescription().trim().isEmpty(), "Enter a mission description.");
        require(CATEGORIES.contains(mission.getCategory()), "Choose Java, SQL, or DSA as the category.");
        require(DIFFICULTIES.contains(mission.getDifficulty()), "Choose a valid difficulty.");
        String[] options = {mission.getOptionA(), mission.getOptionB(), mission.getOptionC(), mission.getOptionD()};
        for (String option : options) require(option != null && !option.trim().isEmpty() && option.trim().length() <= 255,
                "All four answer options are required and must be 255 characters or fewer.");
        require(new java.util.HashSet<>(java.util.Arrays.asList(options)).size() == 4, "Each answer option must be distinct.");
        require(ANSWERS.contains(mission.getCorrectAnswer()), "Choose which answer is correct.");
        require(mission.getXpReward() > 0 && mission.getXpReward() <= 10_000, "XP reward must be from 1 to 10,000.");
        mission.setTitle(mission.getTitle().trim());
        mission.setDescription(mission.getDescription().trim());
        mission.setOptionA(mission.getOptionA().trim());
        mission.setOptionB(mission.getOptionB().trim());
        mission.setOptionC(mission.getOptionC().trim());
        mission.setOptionD(mission.getOptionD().trim());
        if (mission.getMissionId() > 0) {
            int updated = missionDAO.update(mission);
            if (updated == 0) throw new ValidationException("This mission has already been attempted or archived, so its history is frozen.");
            return updated;
        }
        missionDAO.insert(mission);
        return mission.getMissionId();
    }

    private void require(boolean condition, String message) throws ValidationException {
        if (!condition) throw new ValidationException(message);
    }

    public static class ValidationException extends Exception {
        public ValidationException(String message) { super(message); }
    }
}
