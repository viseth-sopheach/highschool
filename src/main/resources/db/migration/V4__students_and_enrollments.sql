-- ============================================================
-- V4: Students and student enrollments
-- ============================================================

CREATE TABLE students (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id         BIGINT NOT NULL REFERENCES users(id),
    student_code    VARCHAR(20) NOT NULL,
    khmer_name      VARCHAR(150) NOT NULL,
    english_name    VARCHAR(150),
    dob             DATE,
    gender          VARCHAR(10),
    phone           VARCHAR(20),
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_students_user UNIQUE (user_id),
    CONSTRAINT uq_students_code UNIQUE (student_code)
);

CREATE TRIGGER trg_students_updated_at
    BEFORE UPDATE ON students
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE student_enrollments (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id          BIGINT NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    school_class_id     BIGINT NOT NULL REFERENCES school_classes(id) ON DELETE CASCADE,
    enrollment_date     DATE NOT NULL DEFAULT CURRENT_DATE,
    status              VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
                            CHECK (status IN ('ACTIVE', 'TRANSFERRED', 'WITHDRAWN', 'GRADUATED')),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_student_class_enrollment UNIQUE (student_id, school_class_id)
);

CREATE INDEX idx_student_enrollments_student ON student_enrollments(student_id);
CREATE INDEX idx_student_enrollments_class ON student_enrollments(school_class_id);

CREATE TRIGGER trg_student_enrollments_updated_at
    BEFORE UPDATE ON student_enrollments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
