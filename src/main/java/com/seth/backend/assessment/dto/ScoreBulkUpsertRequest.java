package com.seth.backend.assessment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/** Whole-class grade entry for one assessment in a single transaction. */
public record ScoreBulkUpsertRequest(
        @NotEmpty @Valid List<ScoreEntryRequest> entries
) {}