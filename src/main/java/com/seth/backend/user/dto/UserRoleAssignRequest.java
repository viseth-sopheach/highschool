package com.seth.backend.user.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

/** Replaces the user's full role set (not additive) — matches PUT semantics. */
public record UserRoleAssignRequest(
        @NotEmpty Set<String> roleNames
) {}