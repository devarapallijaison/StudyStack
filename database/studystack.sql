-- StudyStack schema for MySQL 8.0.16+ (CHECK constraints are enforced from 8.0.16).
-- Mission removal is a soft delete (is_active = FALSE), preserving submissions.
CREATE DATABASE IF NOT EXISTS studystack_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE studystack_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    total_xp INT UNSIGNED NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    CONSTRAINT uq_users_username UNIQUE (username),
    CONSTRAINT uq_users_email UNIQUE (email),
    CONSTRAINT chk_users_full_name CHECK (CHAR_LENGTH(TRIM(full_name)) > 0),
    CONSTRAINT chk_users_username CHECK (CHAR_LENGTH(TRIM(username)) > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS admins (
    admin_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (admin_id),
    CONSTRAINT uq_admins_username UNIQUE (username)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS missions (
    mission_id INT UNSIGNED NOT NULL AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category ENUM('Java', 'SQL', 'DSA') NOT NULL,
    difficulty ENUM('Easy', 'Medium', 'Hard') NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer ENUM('A', 'B', 'C', 'D') NOT NULL,
    xp_reward INT UNSIGNED NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (mission_id),
    KEY idx_missions_active_category_difficulty (is_active, category, difficulty),
    KEY idx_missions_created_at (created_at),
    CONSTRAINT chk_missions_title CHECK (CHAR_LENGTH(TRIM(title)) > 0),
    CONSTRAINT chk_missions_xp CHECK (xp_reward > 0)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS mission_submissions (
    submission_id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id INT UNSIGNED NOT NULL,
    mission_id INT UNSIGNED NOT NULL,
    selected_answer ENUM('A', 'B', 'C', 'D') NOT NULL,
    is_correct BOOLEAN NOT NULL,
    xp_earned INT UNSIGNED NOT NULL DEFAULT 0,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (submission_id),
    CONSTRAINT uq_submission_user_mission UNIQUE (user_id, mission_id),
    KEY idx_submissions_leaderboard_lookup (user_id, is_correct),
    KEY idx_submissions_recent (submitted_at),
    CONSTRAINT fk_submissions_user FOREIGN KEY (user_id)
        REFERENCES users (user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_submissions_mission FOREIGN KEY (mission_id)
        REFERENCES missions (mission_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_submission_xp CHECK (is_correct = TRUE OR xp_earned = 0)
) ENGINE=InnoDB;

-- Repeatable sample missions: the answer key stays in this database table and is
-- omitted by MissionDAO student-facing queries. No student accounts/passwords seeded.
INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'Java: Primitive Types', 'Which Java type stores a single true or false value?', 'Java', 'Easy',
       'boolean', 'String', 'char', 'int', 'A', 10
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'Java: Primitive Types');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'Java: Collection Choice', 'Which collection preserves insertion order and permits duplicate elements?', 'Java', 'Medium',
       'HashSet', 'ArrayList', 'TreeSet', 'Map', 'B', 25
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'Java: Collection Choice');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'Java: Checked Exceptions', 'What must a method do when it calls a checked exception that it does not handle?', 'Java', 'Hard',
       'Ignore it at runtime', 'Declare it with throws', 'Convert it to an Error', 'Mark the method final', 'B', 50
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'Java: Checked Exceptions');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'SQL: Select Rows', 'Which query returns every column from a table named students?', 'SQL', 'Easy',
       'GET ALL students;', 'SELECT * FROM students;', 'SHOW students;', 'READ students;', 'B', 10
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'SQL: Select Rows');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'SQL: Grouped Counts', 'Which clause filters grouped results after GROUP BY has been applied?', 'SQL', 'Medium',
       'WHERE', 'HAVING', 'ORDER BY', 'LIMIT', 'B', 25
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'SQL: Grouped Counts');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'SQL: Keep Unmatched Rows', 'Which join returns every row from the left table, even when there is no matching right row?', 'SQL', 'Hard',
       'INNER JOIN', 'CROSS JOIN', 'LEFT JOIN', 'NATURAL JOIN', 'C', 50
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'SQL: Keep Unmatched Rows');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'DSA: Stack Order', 'Which order describes how a stack removes its elements?', 'DSA', 'Easy',
       'FIFO', 'LIFO', 'Sorted order', 'Random order', 'B', 10
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'DSA: Stack Order');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'DSA: Binary Search', 'What is the worst-case time complexity of binary search on a sorted array?', 'DSA', 'Medium',
       'O(1)', 'O(log n)', 'O(n)', 'O(n log n)', 'B', 25
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'DSA: Binary Search');

INSERT INTO missions (title, description, category, difficulty, option_a, option_b, option_c, option_d, correct_answer, xp_reward)
SELECT 'DSA: Stable Sorting', 'Which statement describes a stable sorting algorithm?', 'DSA', 'Hard',
       'It always runs in O(n)', 'Equal keys keep their original relative order', 'It uses no extra memory', 'It sorts only numeric values', 'B', 50
WHERE NOT EXISTS (SELECT 1 FROM missions WHERE title = 'DSA: Stable Sorting');

-- Never place a plaintext administrator password in this script.
