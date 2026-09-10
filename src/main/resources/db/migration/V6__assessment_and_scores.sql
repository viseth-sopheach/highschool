-- ============================================================
-- V6: Assessment types, assessments, grading scales, and scores
-- ============================================================

CREATE TABLE assessment_types (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(50) NOT NULL,
    weight_default  NUMERIC(5,2),
    CONSTRAINT uq_assessment_types_name UNIQUE (name)
);

CREATE TABLE assessments (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    school_class_id     BIGINT NOT NULL REFERENCES school_classes(id),
    subject_id          BIGINT NOT NULL REFERENCES subjects(id),
    assessment_type_id  BIGINT NOT NULL REFERENCES assessment_types(id),
    title               VARCHAR(150) NOT NULL,
    max_score           NUMERIC(5,2) NOT NULL CHECK (max_score > 0),
    weight              NUMERIC(5,2),
    assessment_date     DATE NOT NULL,
    created_by          BIGINT NOT NULL REFERENCES teachers(id),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_assessments_class_subject ON assessments(school_class_id, subject_id);
CREATE INDEX idx_assessments_date ON assessments(assessment_date);

CREATE TRIGGER trg_assessments_updated_at
    BEFORE UPDATE ON assessments
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE TABLE grading_scales (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    min_score       NUMERIC(5,2) NOT NULL,
    max_score       NUMERIC(5,2) NOT NULL,
    letter_grade    VARCHAR(5) NOT NULL,
    gpa_point       NUMERIC(3,2),
    description     VARCHAR(100),
    CONSTRAINT chk_grading_scales_range CHECK (max_score > min_score)
);

CREATE TABLE scores (
    id                      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    assessment_id           BIGINT NOT NULL REFERENCES assessments(id) ON DELETE CASCADE,
    student_enrollment_id   BIGINT NOT NULL REFERENCES student_enrollments(id),
    score                   NUMERIC(5,2) NOT NULL CHECK (score >= 0),
    remarks                 VARCHAR(255),
    recorded_by             BIGINT NOT NULL REFERENCES teachers(id),
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_scores_assessment_enrollment UNIQUE (assessment_id, student_enrollment_id)
);

CREATE INDEX idx_scores_enrollment ON scores(student_enrollment_id);
CREATE INDEX idx_scores_assessment ON scores(assessment_id);

CREATE TRIGGER trg_scores_updated_at
    BEFORE UPDATE ON scores
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE FUNCTION validate_score_max()
RETURNS TRIGGER AS $$
DECLARE
    v_max NUMERIC(5,2);
BEGIN
    SELECT max_score INTO v_max FROM assessments WHERE id = NEW.assessment_id;
    IF NEW.score > v_max THEN
        RAISE EXCEPTION 'Score % exceeds assessment max_score %', NEW.score, v_max;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_score_max
    BEFORE INSERT OR UPDATE ON scores
    FOR EACH ROW EXECUTE FUNCTION validate_score_max();
