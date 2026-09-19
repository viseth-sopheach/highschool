package com.seth.backend.security;

import com.seth.backend.TestEnvironment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentIdorTest extends TestEnvironment {

   @Autowired
   private MockMvc mockMvc;

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @Test
   @WithMockJwtUser(username = "vuthy.student@school.kh", authorities = {"PERM_STUDENT_READ_OWN"})
   void studentA_cannotReadStudentB_viaIdChange() throws Exception {
      Long studentBId = requiredLong("select id from students where student_code = 'S-0002'");

      mockMvc.perform(get("/api/students/{id}", studentBId))
              .andExpect(status().isForbidden());
   }

   private Long requiredLong(String sql) {
      return jdbcTemplate.queryForObject(sql, Long.class);
   }
}
