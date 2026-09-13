CREATE TABLE schools (
                         id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                         code            VARCHAR(20) NOT NULL,
                         name            VARCHAR(150) NOT NULL,
                         address         VARCHAR(255),
                         phone           VARCHAR(20),
                         is_active       BOOLEAN NOT NULL DEFAULT true,
                         created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                         updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
                         CONSTRAINT uq_schools_code UNIQUE (code)
);

CREATE TRIGGER trg_schools_updated_at
    BEFORE UPDATE ON schools
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

INSERT INTO schools (code, name) VALUES ('DEFAULT', 'Default School');

ALTER TABLE users ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE users SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE users ALTER COLUMN school_id SET NOT NULL;
CREATE INDEX idx_users_school ON users(school_id);

ALTER TABLE academic_years ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE academic_years SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE academic_years ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE academic_years DROP CONSTRAINT uq_academic_years_name;
ALTER TABLE academic_years ADD CONSTRAINT uq_academic_years_school_name UNIQUE (school_id, name);
DROP INDEX IF EXISTS uq_academic_years_current;
CREATE UNIQUE INDEX uq_academic_years_current ON academic_years (school_id) WHERE is_current = true;
CREATE INDEX idx_academic_years_school ON academic_years(school_id);

ALTER TABLE grades ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE grades SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE grades ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE grades DROP CONSTRAINT uq_grades_name;
ALTER TABLE grades DROP CONSTRAINT uq_grades_level;
ALTER TABLE grades ADD CONSTRAINT uq_grades_school_name UNIQUE (school_id, name);
ALTER TABLE grades ADD CONSTRAINT uq_grades_school_level UNIQUE (school_id, level);
CREATE INDEX idx_grades_school ON grades(school_id);

ALTER TABLE study_tracks ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE study_tracks SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE study_tracks ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE study_tracks DROP CONSTRAINT uq_study_tracks_code;
ALTER TABLE study_tracks ADD CONSTRAINT uq_study_tracks_school_code UNIQUE (school_id, code);
CREATE INDEX idx_study_tracks_school ON study_tracks(school_id);

ALTER TABLE subjects ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE subjects SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE subjects ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE subjects DROP CONSTRAINT uq_subjects_code;
ALTER TABLE subjects ADD CONSTRAINT uq_subjects_school_code UNIQUE (school_id, code);
CREATE INDEX idx_subjects_school ON subjects(school_id);

ALTER TABLE assessment_types ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE assessment_types SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE assessment_types ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE assessment_types DROP CONSTRAINT uq_assessment_types_name;
ALTER TABLE assessment_types ADD CONSTRAINT uq_assessment_types_school_name UNIQUE (school_id, name);
CREATE INDEX idx_assessment_types_school ON assessment_types(school_id);

ALTER TABLE grading_scales ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE grading_scales SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE grading_scales ALTER COLUMN school_id SET NOT NULL;
CREATE INDEX idx_grading_scales_school ON grading_scales(school_id);

ALTER TABLE school_classes ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE school_classes SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE school_classes ALTER COLUMN school_id SET NOT NULL;
CREATE INDEX idx_school_classes_school ON school_classes(school_id);

ALTER TABLE teachers ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE teachers SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE teachers ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE teachers DROP CONSTRAINT uq_teachers_code;
ALTER TABLE teachers ADD CONSTRAINT uq_teachers_school_code UNIQUE (school_id, teacher_code);
CREATE INDEX idx_teachers_school ON teachers(school_id);

ALTER TABLE students ADD COLUMN school_id BIGINT REFERENCES schools(id);
UPDATE students SET school_id = (SELECT id FROM schools WHERE code = 'DEFAULT');
ALTER TABLE students ALTER COLUMN school_id SET NOT NULL;
ALTER TABLE students DROP CONSTRAINT uq_students_code;
ALTER TABLE students ADD CONSTRAINT uq_students_school_code UNIQUE (school_id, student_code);
CREATE INDEX idx_students_school ON students(school_id);

INSERT INTO permissions (name, description) VALUES
    ('SCHOOL_MANAGE', 'Create/update schools (system-admin, cross-school)');