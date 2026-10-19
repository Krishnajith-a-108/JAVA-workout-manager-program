

DROP DATABASE IF EXISTS gym_db;
CREATE DATABASE gym_db CHARACTER SET utf8mb4;
USE gym_db;

-- ------------------------------------------------------------
-- 1) users : ONE table that keeps the logins of all 3 user types.
--    role tells the program who is logging in.
-- ------------------------------------------------------------
CREATE TABLE users (
    user_id   INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role      ENUM('CLIENT','TRAINER','ADMIN') NOT NULL
);

-- ------------------------------------------------------------
-- 2) workouts : a trainer assigns a workout to a client.
--    remark = the trainer's instruction for that client.
-- ------------------------------------------------------------
CREATE TABLE workouts (
    workout_id   INT AUTO_INCREMENT PRIMARY KEY,
    client_id    INT  NOT NULL,
    trainer_id   INT  NOT NULL,
    exercise     VARCHAR(100) NOT NULL,
    sets         INT,
    reps         INT,
    workout_date DATE NOT NULL,
    remark       VARCHAR(255),
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id)  REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 3) progress : the client writes down his own progress.
--    The admin reads this table.
-- ------------------------------------------------------------
CREATE TABLE progress (
    progress_id INT AUTO_INCREMENT PRIMARY KEY,
    client_id   INT NOT NULL,
    log_date    DATE NOT NULL,
    weight_kg   DECIMAL(5,2),
    status      VARCHAR(20),      -- DONE / PARTIAL / SKIPPED
    note        VARCHAR(255),
    FOREIGN KEY (client_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ------------------------------------------------------------
-- 4) activities : one row every time a trainer does something.
--    The admin reads this table.
-- ------------------------------------------------------------
CREATE TABLE activities (
    activity_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id     INT NOT NULL,
    action      VARCHAR(255) NOT NULL,
    log_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
--  data tharunna item

-- ============================================================
INSERT INTO users (username, password, full_name, role) VALUES
 ('admin',    'admin123',   'System Admin', 'ADMIN'),
 ('trainer1', 'trainer123', 'Ravi Kumar',   'TRAINER'),
 ('trainer2', 'trainer123', 'Sneha Patel',  'TRAINER'),
 ('client1',  'client123',  'Amit Sharma',  'CLIENT'),
 ('client2',  'client123',  'Priya Nair',   'CLIENT');

-- trainer Ravi (user_id 2) assigns workouts to client Amit (user_id 4)
INSERT INTO workouts (client_id, trainer_id, exercise, sets, reps, workout_date, remark) VALUES
 (4, 2, 'Bench Press',  4, 10, '2026-10-18', 'Keep elbows at 45 degrees'),
 (4, 2, 'Squats',       3, 12, '2026-10-20', 'Go down slowly, warm up first'),
 (5, 2, 'Treadmill Run',1,  1, '2026-10-19', '30 minutes at 8 km/h');

-- client Amit writes his progress
INSERT INTO progress (client_id, log_date, weight_kg, status, note) VALUES
 (4, '2026-10-18', 78.50, 'DONE',    'Completed all sets'),
 (4, '2026-10-20', 77.90, 'PARTIAL', 'Knee pain, did only 2 sets');

-- trainer activities
INSERT INTO activities (user_id, action) VALUES
 (2, 'Assigned workout ''Bench Press'' to client Amit Sharma'),
 (2, 'Assigned workout ''Squats'' to client Amit Sharma');
