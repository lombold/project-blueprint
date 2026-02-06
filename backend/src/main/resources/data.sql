-- Sample test data
INSERT INTO users (username, email) VALUES
('john_doe', 'john@example.com'),
('jane_smith', 'jane@example.com'),
('mike_johnson', 'mike@example.com');

INSERT INTO workouts (user_id, name, description, duration_minutes, exercise_count) VALUES
(1, 'Push Day', 'Chest, shoulders, triceps', 60, 4),
(1, 'Pull Day', 'Back, biceps', 50, 3),
(2, 'Leg Day', 'Quads, hamstrings, calves', 75, 5);

INSERT INTO exercises (workout_id, name, sets, reps, weight_kg, order_index) VALUES
(1, 'Bench Press', 4, 8, 100.0, 1),
(1, 'Incline Dumbbell Press', 3, 10, 35.0, 2),
(1, 'Shoulder Press', 3, 10, 50.0, 3),
(1, 'Tricep Dips', 3, 12, NULL, 4),
(2, 'Deadlifts', 3, 5, 140.0, 1),
(2, 'Barbell Rows', 4, 8, 110.0, 2),
(2, 'Pull-ups', 3, 10, NULL, 3),
(3, 'Squats', 4, 6, 120.0, 1),
(3, 'Leg Press', 3, 10, 300.0, 2),
(3, 'Leg Curls', 3, 12, 80.0, 3),
(3, 'Calf Raises', 4, 15, 150.0, 4),
(3, 'Leg Extensions', 3, 12, 90.0, 5);
