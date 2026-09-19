package com.seth.backend.schoolclass;

import com.seth.backend.TestEnvironment;
import com.seth.backend.exception.BusinessRuleViolationException;
import com.seth.backend.schoolclass.dto.SchoolClassCreateRequest;
import com.seth.backend.schoolclass.service.SchoolClassService;
import com.seth.backend.security.WithMockJwtUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class SchoolClassBusinessRuleTest extends TestEnvironment {

   @Autowired
   private SchoolClassService schoolClassService;

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Test
   @WithMockJwtUser(username = "sokha.teacher@school.kh", authorities = "PERM_CLASS_WRITE")
   void grade11WithoutTrack_isRejected() {
      Long academicYearId = requiredLong("""
              select id from academic_years where name = '2025-2026'
              """);
      Long grade11Id = requiredLong("""
              select id from grades where level = 11 and requires_track = true
              """);

      var request = new SchoolClassCreateRequest(academicYearId, grade11Id, null, "11X", (short) 40);
      assertThrows(BusinessRuleViolationException.class, () -> schoolClassService.create(request));
   }

   @Test
   @WithMockJwtUser(username = "sokha.teacher@school.kh", authorities = "PERM_CLASS_WRITE")
   void grade7WithTrack_isRejected() {
      Long academicYearId = requiredLong("""
              select id from academic_years where name = '2025-2026'
              """);
      Long grade7Id = requiredLong("""
              select id from grades where level = 7 and requires_track = false
              """);
      Long studyTrackId = requiredLong("""
              select id from study_tracks where code = 'SCIENCE'
              """);

      var request = new SchoolClassCreateRequest(academicYearId, grade7Id, studyTrackId, "7X", (short) 40);
      assertThrows(BusinessRuleViolationException.class, () -> schoolClassService.create(request));
   }

   private Long requiredLong(String sql) {
      return jdbcTemplate.queryForObject(sql, Long.class);
   }
}
