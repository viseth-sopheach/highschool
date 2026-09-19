package com.seth.backend.security;

import com.seth.backend.TestEnvironment;
import com.seth.backend.attendance.dto.AttendanceMarkRequest;
import com.seth.backend.attendance.entity.AttendanceStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Asserts a teacher not assigned to a class cannot write attendance/assessments/scores
 * for that class, even with a valid ATTENDANCE_WRITE/ASSESSMENT_WRITE/SCORE_WRITE authority.
 * Requires a test fixture teacher (userId configured below) NOT present in
 * class_teacher_assignments for the target school_class_id.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ClassScopedAuthorizationTest extends TestEnvironment {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Test
   @WithMockJwtUser(username = "dara.teacher@school.kh", authorities = {"PERM_ATTENDANCE_WRITE"})
   void teacherNotAssignedToClass_cannotMarkAttendance() throws Exception {
      Long studentId = requiredLong("select id from students where student_code = 'S-0001'");
      Long schoolClassId = requiredLong("""
              select sc.id
              from school_classes sc
              join academic_years ay on ay.id = sc.academic_year_id
              where sc.name = '7A' and ay.name = '2025-2026'
              """);

      var request = new AttendanceMarkRequest(studentId, schoolClassId, LocalDate.of(2026, 2, 3), AttendanceStatus.PRESENT, null);
      mockMvc.perform(post("/api/attendance")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(asJson(request)))
              .andExpect(status().isForbidden());
   }

   private Long requiredLong(String sql) {
      return jdbcTemplate.queryForObject(sql, Long.class);
   }

   private String asJson(Object o) throws Exception {
      return new com.fasterxml.jackson.databind.ObjectMapper()
              .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
              .writeValueAsString(o);
   }
}
