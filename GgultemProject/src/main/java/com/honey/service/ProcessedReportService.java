package com.honey.service;

import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;

public interface ProcessedReportService {
	// 신고 처리 실행
	Long process(ProcessedReportDTO dto);
	
	PageResponseDTO<ProcessedReportDTO> getAdminList(PageRequestDTO pageRequestDTO);
    ProcessedReportDTO getRead(Long reportId);
}
