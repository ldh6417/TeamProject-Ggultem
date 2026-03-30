package com.honey.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.honey.domain.Member;
import com.honey.domain.ProcessedReport;
import com.honey.domain.Report;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ProcessedReportDTO;
import com.honey.dto.ReportDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.ProcessedReportRepository;
import com.honey.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ProcessedReportServiceImpl implements ProcessedReportService {

	private final ProcessedReportRepository processedRepository;
	private final ReportRepository reportRepository;
	private final MemberRepository memberRepository;
	
	// ⭐ ReportService 주입 (이 이름이 정확해야 빨간 줄이 사라집니다)
	private final ReportService reportService;

	@Override
	public Long process(ProcessedReportDTO dto) {
		Report report = reportRepository.findById(dto.getReportId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 신고 내역입니다."));

		Member targetMember = memberRepository.findById(report.getTargetMemberId())
				.orElseThrow(() -> new IllegalArgumentException("피신고자를 찾을 수 없습니다."));

		Member admin = Member.builder().email(dto.getAdminEmail()).build();

		if (dto.getMemberStatus() != null) {
			targetMember.changeStatus(dto.getMemberStatus());
		}

		ProcessedReport processed = ProcessedReport.builder()
				.report(report)
				.admin(admin)
				.actionNote(dto.getActionNote())
				.reportStatus(dto.getReportStatus()) 
				.build();
		
		processedRepository.save(processed);
		report.changeStatus(1);

		return processed.getProcessedReportId();
	}

	@Override
	public PageResponseDTO<ProcessedReportDTO> getAdminList(PageRequestDTO pageRequestDTO) {
		
		Pageable pageable = PageRequest.of(
				pageRequestDTO.getPage() - 1,
				pageRequestDTO.getSize(),
				Sort.by("reportId").descending()
		);

		Page<Report> result = reportRepository.findAll(pageable);

		List<ProcessedReportDTO> dtoList = result.getContent().stream().map(report -> {
			// ⭐ 여기서 ReportDTO 타입을 확실히 명시해줍니다.
			ReportDTO reportDTO = reportService.read(report.getReportId());

			return ProcessedReportDTO.builder()
					.reportId(report.getReportId())
					.reportDTO(reportDTO) 
					.reportStatus(report.getStatus() == 1 ? "처리완료" : "접수")
					.build();
		}).collect(Collectors.toList());

		return PageResponseDTO.<ProcessedReportDTO>withAll()
				.dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO)
				.totalCount(result.getTotalElements())
				.build();
	}

	@Override
	public ProcessedReportDTO getRead(Long reportId) {
		
		// 1. 신고 원문 정보 가져오기 (ReportService 활용)
		// ⭐ image_934572.jpg의 에러를 잡기 위해 리턴 타입을 명시합니다.
		ReportDTO reportDTO = reportService.read(reportId);

		// 2. 관리자 처리 내역이 있는지 확인
		ProcessedReport processed = processedRepository.findByReport_ReportId(reportId).orElse(null);

		return ProcessedReportDTO.builder()
				.reportId(reportId)
				.reportDTO(reportDTO)
				.actionNote(processed != null ? processed.getActionNote() : "")
				.adminEmail(processed != null && processed.getAdmin() != null ? processed.getAdmin().getEmail() : "")
				.reportStatus(reportDTO.getStatus() == 1 ? "처리완료" : "접수")
				.build();
	}
}