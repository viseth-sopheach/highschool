package com.seth.backend.attendance.service;

import com.seth.backend.attendance.dto.AttendanceBulkMarkRequest;
import com.seth.backend.attendance.dto.AttendanceMarkRequest;
import com.seth.backend.attendance.dto.AttendanceResponse;
import com.seth.backend.attendance.entity.AttendanceRecord;
import com.seth.backend.attendance.mapper.AttendanceMapper;
import com.seth.backend.attendance.repository.AttendanceRecordRepository;
import com.seth.backend.exception.AccessDeniedOnResourceException;
import com.seth.backend.exception.ResourceNotFoundException;
import com.seth.backend.schoolclass.entity.SchoolClass;
import com.seth.backend.schoolclass.repository.SchoolClassRepository;
import com.seth.backend.security.SecurityUtils;
import com.seth.backend.student.entity.Student;
import com.seth.backend.student.repository.StudentRepository;
import com.seth.backend.teacher.entity.Teacher;
import com.seth.backend.teacher.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

   private final AttendanceRecordRepository attendanceRepository;
   private final StudentRepository studentRepository;
   private final SchoolClassRepository schoolClassRepository;
   private final TeacherRepository teacherRepository;
   private final AttendanceMapper mapper;

   public Page<AttendanceResponse> listForStudent(Long studentId, Pageable pageable) {
      return attendanceRepository.findByStudent_IdOrderByAttendanceDateDesc(studentId, pageable)
              .map(mapper::toResponse);
   }

   public List<AttendanceResponse> listForClassOnDate(Long schoolClassId, LocalDate date) {
      return attendanceRepository.findBySchoolClass_IdAndAttendanceDate(schoolClassId, date).stream()
              .map(mapper::toResponse).toList();
   }

   @Transactional
   public AttendanceResponse mark(AttendanceMarkRequest request) {
      Student student = studentRepository.findById(request.studentId())
              .orElseThrow(() -> ResourceNotFoundException.of("Student", request.studentId()));
      SchoolClass schoolClass = schoolClassRepository.findById(request.schoolClassId())
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", request.schoolClassId()));
      Teacher recordedBy = currentTeacherOrThrow();

      AttendanceRecord record = attendanceRepository
              .findByStudent_IdAndSchoolClass_IdAndAttendanceDate(student.getId(), schoolClass.getId(), request.attendanceDate())
              .orElseGet(AttendanceRecord::new);

      record.setStudent(student);
      record.setSchoolClass(schoolClass);
      record.setAttendanceDate(request.attendanceDate());
      record.setStatus(request.status());
      record.setRemarks(request.remarks());
      record.setRecordedBy(recordedBy);

      return mapper.toResponse(attendanceRepository.save(record));
   }

   /**
    * One transaction for the whole class roster, upserting per student.
    * Avoids N separate HTTP round-trips for a ~20-40 student class and keeps
    * the write batched (see hibernate.jdbc.batch_size) instead of N individual
    * commits.
    */
   @Transactional
   public List<AttendanceResponse> markBulk(AttendanceBulkMarkRequest request) {
      SchoolClass schoolClass = schoolClassRepository.findById(request.schoolClassId())
              .orElseThrow(() -> ResourceNotFoundException.of("SchoolClass", request.schoolClassId()));
      Teacher recordedBy = currentTeacherOrThrow();

      return request.entries().stream().map(entry -> {
         Student student = studentRepository.findById(entry.studentId())
                 .orElseThrow(() -> ResourceNotFoundException.of("Student", entry.studentId()));

         AttendanceRecord record = attendanceRepository
                 .findByStudent_IdAndSchoolClass_IdAndAttendanceDate(student.getId(), schoolClass.getId(), request.attendanceDate())
                 .orElseGet(AttendanceRecord::new);

         record.setStudent(student);
         record.setSchoolClass(schoolClass);
         record.setAttendanceDate(request.attendanceDate());
         record.setStatus(entry.status());
         record.setRemarks(entry.remarks());
         record.setRecordedBy(recordedBy);

         return mapper.toResponse(attendanceRepository.save(record));
      }).toList();
   }

   /** IDOR guard mirroring StudentEnrollmentService.assertOwnedByCurrentUser. */
   public void assertOwnedByCurrentUser(Long studentId) {
      Student student = studentRepository.findWithUserById(studentId)
              .orElseThrow(() -> ResourceNotFoundException.of("Student", studentId));
      if (!student.getUser().getId().equals(SecurityUtils.currentUserId())) {
         throw new AccessDeniedOnResourceException("Not authorized to view this student's attendance.");
      }
   }

   private Teacher currentTeacherOrThrow() {
      Long userId = SecurityUtils.currentUserId();
      return teacherRepository.findWithUserByUserId(userId)
              .orElseThrow(() -> new ResourceNotFoundException("No teacher profile linked to this account."));
   }
}