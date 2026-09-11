-- ============================================================
-- V9: Trigram indexes for substring ('%term%') search performance
-- ============================================================
-- A LIKE '%term%' predicate (leading wildcard) can't use a plain B-tree
-- index — Postgres falls back to a sequential scan. That's invisible at
-- a few hundred rows and increasingly slow as teachers/classes/students
-- grow into the thousands. pg_trgm lets a GIN index serve substring
-- search instead.

CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX idx_teachers_khmer_name_trgm ON teachers USING gin (khmer_name gin_trgm_ops);
CREATE INDEX idx_teachers_english_name_trgm ON teachers USING gin (english_name gin_trgm_ops);
CREATE INDEX idx_teachers_code_trgm ON teachers USING gin (teacher_code gin_trgm_ops);

CREATE INDEX idx_school_classes_name_trgm ON school_classes USING gin (name gin_trgm_ops);