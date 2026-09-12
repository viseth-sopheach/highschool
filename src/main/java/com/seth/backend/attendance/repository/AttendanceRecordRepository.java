package com.seth.backend.attendance.repository;

import com.seth.backend.attendance.entity.AttendanceRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

   @EntityGraph(attributePaths = {"schoolClass"})
   Page<AttendanceRecord> findByStudent_IdOrderByAttendanceDateDesc(Long studentId, Pageable pageable);

   @EntityGraph(attributePaths = {"student", "student.user"})
   List<AttendanceRecord> findBySchoolClass_IdAndAttendanceDate(Long schoolClassId, LocalDate date);

   Optional<AttendanceRecord> findByStudent_IdAndSchoolClass_IdAndAttendanceDate(
           Long studentId, Long schoolClassId, LocalDate date);

   boolean existsByStudent_IdAndSchoolClass_IdAndAttendanceDate(
           Long studentId, Long schoolClassId, LocalDate date);
}