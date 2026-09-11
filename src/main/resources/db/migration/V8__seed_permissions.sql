-- ============================================================
-- V8: Permission catalog + role_permission assignment
-- ============================================================

INSERT INTO permissions (name, description) VALUES
                                                ('STUDENT_READ',        'Read any student record'),
                                                ('STUDENT_READ_OWN',    'Read own student record'),
                                                ('STUDENT_WRITE',       'Create/update student records'),
                                                ('TEACHER_READ',        'Read any teacher record'),
                                                ('TEACHER_WRITE',       'Create/update teacher records'),
                                                ('CLASS_READ',          'Read class/roster data'),
                                                ('CLASS_WRITE',         'Create/update classes'),
                                                ('ENROLLMENT_READ',     'Read enrollment records'),
                                                ('ENROLLMENT_WRITE',    'Create/update enrollment records'),
                                                ('ATTENDANCE_READ',     'Read attendance for any class'),
                                                ('ATTENDANCE_READ_OWN', 'Read own attendance'),
                                                ('ATTENDANCE_WRITE',    'Record/update attendance'),
                                                ('ASSESSMENT_READ',     'Read assessment definitions'),
                                                ('ASSESSMENT_WRITE',    'Create/update assessments'),
                                                ('SCORE_READ',          'Read scores for any student'),
                                                ('SCORE_READ_OWN',      'Read own scores'),
                                                ('SCORE_WRITE',         'Record/update scores'),
                                                ('USER_READ',           'Read user accounts'),
                                                ('USER_MANAGE',         'Create/update/lock user accounts'),
                                                ('ROLE_MANAGE',         'Assign/revoke roles'),
                                                ('AUDIT_READ',          'Read audit logs');

WITH rp AS (
    SELECT r.id AS role_id, p.id AS permission_id, r.name AS role_name, p.name AS perm_name
    FROM roles r CROSS JOIN permissions p
)
INSERT INTO role_permissions (role_id, permission_id)
SELECT role_id, permission_id FROM rp WHERE
    (role_name = 'STUDENT' AND perm_name IN ('STUDENT_READ_OWN','ATTENDANCE_READ_OWN','SCORE_READ_OWN'))
                                         OR (role_name = 'TEACHER' AND perm_name IN (
                                                                                     'STUDENT_READ','CLASS_READ','ENROLLMENT_READ',
                                                                                     'ATTENDANCE_READ','ATTENDANCE_WRITE',
                                                                                     'ASSESSMENT_READ','ASSESSMENT_WRITE',
                                                                                     'SCORE_READ','SCORE_WRITE'))
                                         OR (role_name = 'VICE_PRINCIPAL' AND perm_name IN (
                                                                                            'STUDENT_READ','STUDENT_WRITE','TEACHER_READ',
                                                                                            'CLASS_READ','CLASS_WRITE','ENROLLMENT_READ','ENROLLMENT_WRITE',
                                                                                            'ATTENDANCE_READ','ATTENDANCE_WRITE',
                                                                                            'ASSESSMENT_READ','ASSESSMENT_WRITE',
                                                                                            'SCORE_READ','SCORE_WRITE','USER_READ'))
                                         OR (role_name = 'PRINCIPAL');