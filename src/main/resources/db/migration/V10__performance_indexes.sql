-- V10: Additional composite indexes for high-volume query paths


-- Attendance: per-student chronological lookups ("my attendance history")
CREATE INDEX idx_attendance_student_date ON attendance_records(student_id, attendance_date DESC);

-- Scores: per-enrollment history across all assessments
CREATE INDEX idx_scores_enrollment_created ON scores(student_enrollment_id, created_at DESC);

-- Assessments: teacher's own created assessments (grading workload views)
CREATE INDEX idx_assessments_created_by ON assessments(created_by);

-- Class teacher assignments: homeroom + subject lookups already covered by
-- uq_class_teacher_homeroom / idx_cta_teacher / idx_cta_class from V3.

-- Enrollments: active-status filter is used on nearly every roster query
CREATE INDEX idx_enrollments_class_status ON student_enrollments(school_class_id, status);
CREATE INDEX idx_enrollments_student_status ON student_enrollments(student_id, status);

-- Audit logs: entity lookup ("history for this record")
CREATE INDEX idx_audit_logs_entity ON audit_logs(entity_type, entity_id);