package com.honey.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.honey.domain.ProcessedReport;

public interface ProcessedReportRepository extends JpaRepository<ProcessedReport, Long> {
    // 기본적인 조치는 상속받은 메서드로 충분.
	
	// ProcessedReportRepository.java
	Optional<ProcessedReport> findByReport_ReportId(Long reportId);
}