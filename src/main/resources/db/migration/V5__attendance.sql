-- ============================================================
-- V5: Attendance
-- ============================================================

CREATE TABLE attendance_records (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    student_id          BIGINT NOT NULL REFERENCES students(id),
    school_class_id     BIGINT NOT NULL REFERENCES school_classes(id),
    attendance_date     DATE NOT NULL,
    status              VARCHAR(20) NOT NULL
                            CHECK (status IN ('PRESENT','ABSENT','LATE','EXCUSED')),
    recorded_by         BIGINT NOT NULL REFERENCES teachers(id),
    remarks             VARCHAR(255),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_attendance_student_class_date UNIQUE (student_id, school_class_id, attendance_date)
);

CREATE INDEX idx_attendance_class_date ON attendance_records(school_class_id, attendance_date);
CREATE INDEX idx_attendance_student ON attendance_records(student_id);

CREATE TRIGGER trg_attendance_updated_at
    BEFORE UPDATE ON attendance_records
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();
