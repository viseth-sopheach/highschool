-- ============================================================
-- V3: Subjects, subject applicability, teachers, teaching assignments
-- ============================================================

CREATE TABLE subjects (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(20) NOT NULL,
    name_km         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL,
    is_active       BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uq_subjects_code UNIQUE (code)
);

CREATE TABLE grade_subjects (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    grade_id            BIGINT NOT NULL REFERENCES grades(id),
    study_track_id      BIGINT REFERENCES study_tracks(id),
    subject_id          BIGINT NOT NULL REFERENCES subjects(id)
);

CREATE UNIQUE INDEX uq_grade_subjects_with_track
    ON grade_subjects (grade_id, study_track_id, subject_id)
    WHERE study_track_id IS NOT NULL;

CREATE UNIQUE INDEX uq_grade_subjects_without_track
    ON grade_subjects (grade_id, subject_id)
    WHERE study_track_id IS NULL;

CREATE INDEX idx_grade_subjects_grade ON grade_subjects(grade_id);

CREATE TABLE class_subjects (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    school_class_id     BIGINT NOT NULL REFERENCES school_classes(id) ON DELETE CASCADE,
    subject_id          BIGINT NOT NULL REFERENCES subjects(id),
    CONSTRAINT uq_class_subjects UNIQUE (school_class_id, subject_id)
);

CREATE INDEX idx_class_subjects_class ON class_subjects(school_class_id);

CREATE TABLE teachers (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    teacher_code    VARCHAR(20) NOT NULL,
    khmer_name      VARCHAR(150) NOT NULL,
    english_name    VARCHAR(150),
    phone           VARCHAR(20),
    hire_date       DATE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_teachers_user UNIQUE (user_id),
    CONSTRAINT uq_teachers_code UNIQUE (teacher_code)
);

CREATE TRIGGER trg_teachers_updated_at
    BEFORE UPDATE ON teachers
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE class_teacher_assignments (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    school_class_id     BIGINT NOT NULL REFERENCES school_classes(id) ON DELETE CASCADE,
    teacher_id          BIGINT NOT NULL REFERENCES teachers(id),
    subject_id          BIGINT REFERENCES subjects(id),
    is_homeroom         BOOLEAN NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_class_teacher_subject UNIQUE (school_class_id, teacher_id, subject_id)
);

CREATE UNIQUE INDEX uq_class_teacher_homeroom
    ON class_teacher_assignments (school_class_id)
    WHERE is_homeroom = true;

CREATE INDEX idx_cta_teacher ON class_teacher_assignments(teacher_id);
CREATE INDEX idx_cta_class ON class_teacher_assignments(school_class_id);
