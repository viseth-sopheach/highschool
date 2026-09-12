package com.seth.backend.academic.repository;

import com.seth.backend.academic.entity.GradingScale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradingScaleRepository extends JpaRepository<GradingScale, Long> {
   List<GradingScale> findAllByOrderByMinScoreDesc();
}