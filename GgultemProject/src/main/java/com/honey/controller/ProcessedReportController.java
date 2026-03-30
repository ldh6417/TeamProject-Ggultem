package com.honey.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;
import com.honey.service.ProcessedReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/report")
@RequiredArgsConstructor
public class ProcessedReportController {

    private final ProcessedReportService processedService;

    // 상세 보기 (신고내용 + 처리내역)
    @GetMapping("/{reportId}")
    public ProcessedReportDTO read(@PathVariable("reportId") Long reportId) {
        return processedService.getRead(reportId);
    }
    
    // 리스트 조회
    @GetMapping("/list")
    public PageResponseDTO<ProcessedReportDTO> list(PageRequestDTO pageRequestDTO) {
        return processedService.getAdminList(pageRequestDTO);
    }
}