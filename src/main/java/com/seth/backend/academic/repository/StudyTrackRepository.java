package com.seth.backend.academic.repository;

import com.seth.backend.academic.entity.StudyTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyTrackRepository extends JpaRepository<StudyTrack, Long> {
   List<StudyTrack> findBySchool_Id(Long schoolId);
}