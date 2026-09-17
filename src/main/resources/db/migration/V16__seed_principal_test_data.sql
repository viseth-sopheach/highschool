-- src/main/resources/db/migration/V16__seed_principal_test_data.sql

-- ============================================================
-- Grading scale (DEFAULT school)
-- ============================================================
INSERT INTO grading_scales (school_id, min_score, max_score, letter_grade, gpa_point, description)
SELECT s.id, v.min_score, v.max_score, v.letter_grade, v.gpa_point, v.description
FROM schools s
         CROSS JOIN (VALUES
                         (90.00, 100.00, 'A', 4.00, 'Excellent'),
                         (80.00, 89.99,  'B', 3.00, 'Good'),
                         (70.00, 79.99,  'C', 2.00, 'Average'),
                         (60.00, 69.99,  'D', 1.00, 'Below Average'),
                         (0.00,  59.99,  'F', 0.00, 'Fail')
) AS v(min_score, max_score, letter_grade, gpa_point, description)
WHERE s.code = 'DEFAULT';

-- ============================================================
-- Subjects (DEFAULT school)
-- ============================================================
INSERT INTO subjects (school_id, code, name_km, name_en, is_active)
SELECT s.id, v.code, v.name_km, v.name_en, true
FROM schools s
         CROSS JOIN (VALUES
                         ('KHM', N'ភាសាខ្មែរ', 'Khmer'),
                         ('MATH', N'គណិតវិទ្យា', 'Mathematics'),
                         ('PHYS', N'រូបវិទ្យា', 'Physics'),
                         ('CHEM', N'គីមីវិទ្យា', 'Chemistry'),
                         ('BIO', N'ជីវវិទ្យា', 'Biology'),
                         ('ENG', N'ភាសាអង់គ្លេស', 'English'),
                         ('HIST', N'ប្រវត្តិវិទ្យា', 'History'),
                         ('GEO', N'ភូមិវិទ្យា', 'Geography'),
                         ('CIVIC', N'អប់រំពលរដ្ឋ', 'Civics'),
                         ('EARTH', N'ផែនដីវិទ្យា', 'Earth Science')
) AS v(code, name_km, name_en)
WHERE s.code = 'DEFAULT';

-- ============================================================
-- Grade-subject mapping
-- ============================================================
INSERT INTO grade_subjects (grade_id, study_track_id, subject_id)
SELECT g.id, NULL, sub.id
FROM grades g
         JOIN subjects sub ON sub.school_id = g.school_id
WHERE g.level BETWEEN 7 AND 10
  AND sub.code IN ('KHM','MATH','ENG','HIST','GEO','PHYS','CHEM','BIO');

INSERT INTO grade_subjects (grade_id, study_track_id, subject_id)
SELECT g.id, st.id, sub.id
FROM grades g
         JOIN study_tracks st ON st.school_id = g.school_id AND st.code = 'SCIENCE'
         JOIN subjects sub ON sub.school_id = g.school_id
WHERE g.level IN (11, 12)
  AND sub.code IN ('KHM','MATH','ENG','PHYS','CHEM','BIO');

INSERT INTO grade_subjects (grade_id, study_track_id, subject_id)
SELECT g.id, st.id, sub.id
FROM grades g
         JOIN study_tracks st ON st.school_id = g.school_id AND st.code = 'SOCIAL_SCIENCE'
         JOIN subjects sub ON sub.school_id = g.school_id
WHERE g.level IN (11, 12)
  AND sub.code IN ('KHM','MATH','ENG','HIST','GEO','CIVIC');

-- ============================================================
-- Academic year 2025-2026 (current)
-- ============================================================
INSERT INTO academic_years (school_id, name, start_date, end_date, is_current)
SELECT s.id, '2025-2026', DATE '2025-10-01', DATE '2026-07-31', true
FROM schools s
WHERE s.code = 'DEFAULT';

-- ============================================================
-- Teacher users + profiles
-- ============================================================
INSERT INTO users (school_id, username, email, password_hash, status)
SELECT s.id, v.username, v.username,
       '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKNmPeoJ0FoJvSK',
       'ACTIVE'
FROM schools s
         CROSS JOIN (VALUES
                         ('sokha.teacher@school.kh'),
                         ('dara.teacher@school.kh'),
                         ('mony.teacher@school.kh')
) AS v(username)
WHERE s.code = 'DEFAULT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'TEACHER'
WHERE u.username IN ('sokha.teacher@school.kh', 'dara.teacher@school.kh', 'mony.teacher@school.kh');

INSERT INTO teachers (school_id, user_id, teacher_code, khmer_name, english_name, phone, hire_date)
SELECT u.school_id, u.id, v.teacher_code, v.khmer_name, v.english_name, v.phone, v.hire_date
FROM users u
         JOIN (VALUES
                   ('sokha.teacher@school.kh', 'T-0001', N'សុខា', 'Sokha Chan', '012345001', DATE '2020-09-01'),
                   ('dara.teacher@school.kh',  'T-0002', N'ដារា',  'Dara Pich',  '012345002', DATE '2019-09-01'),
                   ('mony.teacher@school.kh',  'T-0003', N'មុន្នី', 'Mony Sok',   '012345003', DATE '2021-09-01')
) AS v(username, teacher_code, khmer_name, english_name, phone, hire_date)
              ON u.username = v.username;

-- ============================================================
-- School classes for 2025-2026
-- ============================================================
INSERT INTO school_classes (school_id, academic_year_id, grade_id, study_track_id, name, capacity)
SELECT y.school_id, y.id, g.id, NULL, v.name, 40
FROM academic_years y
         CROSS JOIN (VALUES (7, '7A'), (7, '7B')) AS v(level, name)
         JOIN grades g ON g.school_id = y.school_id AND g.level = v.level
WHERE y.name = '2025-2026';

INSERT INTO school_classes (school_id, academic_year_id, grade_id, study_track_id, name, capacity)
SELECT y.school_id, y.id, g.id, st.id, '11A', 40
FROM academic_years y
         JOIN grades g ON g.school_id = y.school_id AND g.level = 11
         JOIN study_tracks st ON st.school_id = y.school_id AND st.code = 'SCIENCE'
WHERE y.name = '2025-2026';

INSERT INTO school_classes (school_id, academic_year_id, grade_id, study_track_id, name, capacity)
SELECT y.school_id, y.id, g.id, st.id, '12A', 40
FROM academic_years y
         JOIN grades g ON g.school_id = y.school_id AND g.level = 12
         JOIN study_tracks st ON st.school_id = y.school_id AND st.code = 'SOCIAL_SCIENCE'
WHERE y.name = '2025-2026';

-- ============================================================
-- Class-subject assignments (mirror grade_subjects for each class)
-- ============================================================
INSERT INTO class_subjects (school_class_id, subject_id)
SELECT sc.id, gs.subject_id
FROM school_classes sc
         JOIN grade_subjects gs
              ON gs.grade_id = sc.grade_id
                  AND (gs.study_track_id IS NOT DISTINCT FROM sc.study_track_id)
         JOIN academic_years y ON y.id = sc.academic_year_id AND y.name = '2025-2026';

-- ============================================================
-- Class-teacher assignments: homeroom
-- 7A -> T-0001, 11A -> T-0002, 12A -> T-0003
-- ============================================================
INSERT INTO class_teacher_assignments (school_class_id, teacher_id, subject_id, is_homeroom)
SELECT sc.id, t.id, NULL, true
FROM school_classes sc
         JOIN academic_years y ON y.id = sc.academic_year_id AND y.name = '2025-2026'
         JOIN (VALUES ('7A', 'T-0001'), ('11A', 'T-0002'), ('12A', 'T-0003')) AS v(class_name, teacher_code)
              ON sc.name = v.class_name
         JOIN teachers t ON t.teacher_code = v.teacher_code;

-- ============================================================
-- Class-teacher assignments: subject teaching
-- T-0001 teaches Math to all classes
-- ============================================================
INSERT INTO class_teacher_assignments (school_class_id, teacher_id, subject_id, is_homeroom)
SELECT sc.id, t.id, sub.id, false
FROM school_classes sc
         JOIN academic_years y ON y.id = sc.academic_year_id AND y.name = '2025-2026'
         JOIN class_subjects cs ON cs.school_class_id = sc.id
         JOIN subjects sub ON sub.id = cs.subject_id AND sub.code = 'MATH'
         JOIN teachers t ON t.teacher_code = 'T-0001';

-- ============================================================
-- Student users + profiles
-- ============================================================
INSERT INTO users (school_id, username, email, password_hash, status)
SELECT s.id, v.username, v.username,
       '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewKNmPeoJ0FoJvSK',
       'ACTIVE'
FROM schools s
         CROSS JOIN (VALUES
                         ('vuthy.student@school.kh'),
                         ('sreymom.student@school.kh'),
                         ('pisey.student@school.kh'),
                         ('rithy.student@school.kh'),
                         ('chanda.student@school.kh')
) AS v(username)
WHERE s.code = 'DEFAULT';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
         JOIN roles r ON r.name = 'STUDENT'
WHERE u.username IN (
                     'vuthy.student@school.kh', 'sreymom.student@school.kh', 'pisey.student@school.kh',
                     'rithy.student@school.kh', 'chanda.student@school.kh'
    );

INSERT INTO students (school_id, user_id, student_code, khmer_name, english_name, dob, gender, phone)
SELECT u.school_id, u.id, v.student_code, v.khmer_name, v.english_name, v.dob, v.gender, v.phone
FROM users u
         JOIN (VALUES
                   ('vuthy.student@school.kh',   'S-0001', N'វុទ្ធី',  'Vuthy Meas',  DATE '2011-03-14', 'MALE',   '098111001'),
                   ('sreymom.student@school.kh', 'S-0002', N'ស្រីមុំ', 'Sreymom Ly',  DATE '2011-06-22', 'FEMALE', '098111002'),
                   ('pisey.student@school.kh',   'S-0003', N'ពិសី',   'Pisey Chea',  DATE '2009-01-05', 'FEMALE', '098111003'),
                   ('rithy.student@school.kh',   'S-0004', N'រិទ្ធី',  'Rithy Ouk',   DATE '2008-11-30', 'MALE',   '098111004'),
                   ('chanda.student@school.kh',  'S-0005', N'ចន្ទដា', 'Chanda Sok',  DATE '2008-08-17', 'FEMALE', '098111005')
) AS v(username, student_code, khmer_name, english_name, dob, gender, phone)
              ON u.username = v.username;

-- ============================================================
-- Enrollments for 2025-2026
-- ============================================================
INSERT INTO student_enrollments (student_id, school_class_id, academic_year_id, enrollment_date, status, is_class_president)
SELECT st.id, sc.id, sc.academic_year_id, DATE '2025-10-01', 'ACTIVE', v.is_president
FROM students st
         JOIN (VALUES
                   ('S-0001', '7A',  true),
                   ('S-0002', '7A',  false),
                   ('S-0003', '11A', true),
                   ('S-0004', '12A', false),
                   ('S-0005', '12A', true)
) AS v(student_code, class_name, is_president) ON st.student_code = v.student_code
         JOIN school_classes sc ON sc.name = v.class_name
         JOIN academic_years y ON y.id = sc.academic_year_id AND y.name = '2025-2026';

-- ============================================================
-- Assessments (Math midterm for each class) + Scores
-- ============================================================
INSERT INTO assessments (school_class_id, subject_id, assessment_type_id, title, max_score, weight, assessment_date, created_by)
SELECT sc.id, sub.id, at.id, 'Midterm Exam - Mathematics', 100.00, 30.00, DATE '2026-01-15', t.id
FROM school_classes sc
         JOIN academic_years y ON y.id = sc.academic_year_id AND y.name = '2025-2026'
         JOIN class_subjects cs ON cs.school_class_id = sc.id
         JOIN subjects sub ON sub.id = cs.subject_id AND sub.code = 'MATH'
         JOIN assessment_types at ON at.name = 'MIDTERM'
    JOIN teachers t ON t.teacher_code = 'T-0001';

INSERT INTO scores (assessment_id, student_enrollment_id, score, remarks, recorded_by)
SELECT a.id, se.id, v.score, v.remarks, t.id
FROM assessments a
         JOIN class_subjects cs ON cs.school_class_id = a.school_class_id AND cs.subject_id = a.subject_id
         JOIN student_enrollments se ON se.school_class_id = a.school_class_id
         JOIN students st ON st.id = se.student_id
         JOIN (VALUES
                   ('S-0001', 88.50, 'Good work'::varchar),
                   ('S-0002', 76.00, NULL::varchar),
                   ('S-0003', 92.00, 'Excellent'::varchar),
                   ('S-0004', 65.50, NULL::varchar),
                   ('S-0005', 81.00, NULL::varchar)
) AS v(student_code, score, remarks) ON st.student_code = v.student_code
         JOIN teachers t ON t.teacher_code = 'T-0001'
WHERE a.title = 'Midterm Exam - Mathematics';

-- ============================================================
-- Attendance for one date per class
-- ============================================================
INSERT INTO attendance_records (student_id, school_class_id, attendance_date, status, recorded_by, remarks)
SELECT st.id, se.school_class_id, DATE '2026-02-02', v.status, t.id, NULL
FROM student_enrollments se
         JOIN students st ON st.id = se.student_id
         JOIN academic_years y ON y.id = se.academic_year_id AND y.name = '2025-2026'
         JOIN (VALUES
                   ('S-0001', 'PRESENT'),
                   ('S-0002', 'LATE'),
                   ('S-0003', 'PRESENT'),
                   ('S-0004', 'ABSENT'),
                   ('S-0005', 'PRESENT')
) AS v(student_code, status) ON st.student_code = v.student_code
         JOIN teachers t ON t.teacher_code = 'T-0001';