package com.seth.backend.academic.repository;

import com.seth.backend.academic.entity.StudyTrack;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyTrackRepository extends JpaRepository<StudyTrack, Long> {
}