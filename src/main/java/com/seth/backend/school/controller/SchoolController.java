package com.seth.backend.school.controller;

import com.seth.backend.common.dto.PageResponse;
import com.seth.backend.common.web.PageableUtils;
import com.seth.backend.school.dto.SchoolCreateRequest;
import com.seth.backend.school.dto.SchoolResponse;
import com.seth.backend.school.dto.SchoolUpdateRequest;
import com.seth.backend.school.service.SchoolService;
import com.seth.backend.security.PermissionConstants;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/schools")
@RequiredArgsConstructor
@PreAuthorize(PermissionConstants.SCHOOL_MANAGE)
public class SchoolController {

   private final SchoolService schoolService;

   @GetMapping
   public PageResponse<SchoolResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
      return PageResponse.from(schoolService.list(PageableUtils.capped(pageable)));
   }

   @GetMapping("/{id}")
   public SchoolResponse getById(@PathVariable Long id) {
      return schoolService.getById(id);
   }

   @PostMapping
   public ResponseEntity<SchoolResponse> create(@Valid @RequestBody SchoolCreateRequest request) {
      SchoolResponse created = schoolService.create(request);
      return ResponseEntity.created(URI.create("/api/schools/" + created.id())).body(created);
   }

   @PutMapping("/{id}")
   public SchoolResponse update(@PathVariable Long id, @Valid @RequestBody SchoolUpdateRequest request) {
      return schoolService.update(id, request);
   }
}