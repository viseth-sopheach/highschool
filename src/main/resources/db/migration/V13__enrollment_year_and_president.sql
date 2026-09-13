-- ============================================================
-- V13: Denormalize academic_year_id onto student_enrollments to
-- close a check-then-act race in StudentEnrollmentService.enroll(),
-- and add class-president designation (scoped per enrollment).
-- ============================================================

ALTER TABLE student_enrollments
    ADD COLUMN academic_year_id BIGINT REFERENCES academic_years(id);

UPDATE student_enrollments se
SET academic_year_id = sc.academic_year_id
    FROM school_classes sc
WHERE se.school_class_id = sc.id;

ALTER TABLE student_enrollments
    ALTER COLUMN academic_year_id SET NOT NULL;

CREATE INDEX idx_enrollments_academic_year ON student_enrollments(academic_year_id);

-- Enforces "one ACTIVE enrollment per student per year" at the DB level,
-- closing the check-then-act race in StudentEnrollmentService.enroll().
CREATE UNIQUE INDEX uq_one_active_enrollment_per_year
    ON student_enrollments (student_id, academic_year_id)
    WHERE status = 'ACTIVE';

ALTER TABLE student_enrollments
    ADD COLUMN is_class_president BOOLEAN NOT NULL DEFAULT false;

-- At most one class president per class (per enrollment set, i.e. per year).
CREATE UNIQUE INDEX uq_class_president_per_class
    ON student_enrollments (school_class_id)
    WHERE is_class_president = true;