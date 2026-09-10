-- ============================================================
-- V2: Academic calendar structure (years, grades, tracks, classes)
-- ============================================================

CREATE TABLE academic_years (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(20) NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    is_current      BOOLEAN NOT NULL DEFAULT false,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_academic_years_name UNIQUE (name),
    CONSTRAINT chk_academic_years_dates CHECK (end_date > start_date)
);

CREATE UNIQUE INDEX uq_academic_years_current
    ON academic_years (is_current)
    WHERE is_current = true;

CREATE TABLE grades (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name            VARCHAR(20) NOT NULL,
    level           SMALLINT NOT NULL,
    requires_track  BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uq_grades_name UNIQUE (name),
    CONSTRAINT uq_grades_level UNIQUE (level),
    CONSTRAINT chk_grades_level CHECK (level BETWEEN 7 AND 12)
);

CREATE TABLE study_tracks (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(30) NOT NULL,
    name_km         VARCHAR(100) NOT NULL,
    name_en         VARCHAR(100) NOT NULL,
    CONSTRAINT uq_study_tracks_code UNIQUE (code)
);

CREATE TABLE school_classes (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    academic_year_id    BIGINT NOT NULL REFERENCES academic_years(id),
    grade_id            BIGINT NOT NULL REFERENCES grades(id),
    study_track_id      BIGINT REFERENCES study_tracks(id),
    name                VARCHAR(10) NOT NULL,
    capacity            SMALLINT,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uq_school_classes_identity
        UNIQUE (academic_year_id, grade_id, study_track_id, name)
);

CREATE INDEX idx_school_classes_year ON school_classes(academic_year_id);
CREATE INDEX idx_school_classes_grade ON school_classes(grade_id);

CREATE TRIGGER trg_school_classes_updated_at
    BEFORE UPDATE ON school_classes
    FOR EACH ROW EXECUTE FUNCTION set_updated_at();

CREATE OR REPLACE FUNCTION validate_school_class_track()
RETURNS TRIGGER AS $$
DECLARE
    v_requires_track BOOLEAN;
BEGIN
    SELECT requires_track INTO v_requires_track FROM grades WHERE id = NEW.grade_id;

    IF v_requires_track AND NEW.study_track_id IS NULL THEN
        RAISE EXCEPTION 'Grade % requires a study track', NEW.grade_id;
    END IF;

    IF NOT v_requires_track AND NEW.study_track_id IS NOT NULL THEN
        RAISE EXCEPTION 'Grade % must not have a study track', NEW.grade_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_validate_school_class_track
    BEFORE INSERT OR UPDATE ON school_classes
    FOR EACH ROW EXECUTE FUNCTION validate_school_class_track();

INSERT INTO grades (name, level, requires_track) VALUES
    ('Grade 7', 7, false),
    ('Grade 8', 8, false),
    ('Grade 9', 9, false),
    ('Grade 10', 10, false),
    ('Grade 11', 11, true),
    ('Grade 12', 12, true);

INSERT INTO study_tracks (code, name_km, name_en) VALUES
    ('SCIENCE', N'វិទ្យាសាស្រ្តពិត', 'Science'),
    ('SOCIAL_SCIENCE', N'វិទ្យាសាស្រ្តសង្គម', 'Social Science');
