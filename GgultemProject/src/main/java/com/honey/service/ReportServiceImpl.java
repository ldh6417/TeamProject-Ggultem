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
import com.honey.domain.Report;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.ReportDTO;
import com.honey.repository.MemberRepository;
import com.honey.repository.ReportRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final MemberRepository memberRepository;

	@Override
	public Long register(ReportDTO reportDTO) {

		// 검증 로직 추가: 기타 신고인데 사유가 없는 경우
		if (Report.TYPE_ETC.equals(reportDTO.getReportType())
				&& (reportDTO.getReason() == null || reportDTO.getReason().trim().isEmpty())) {
			throw new IllegalArgumentException("기타 신고 시 상세 사유를 입력해야 합니다.");
		}

		boolean exists = reportRepository.existsByReporter_EmailAndTargetTypeAndTargetNo(reportDTO.getMemberEmail(),
				reportDTO.getTargetType(), reportDTO.getTargetNo());
		if (exists) {
			throw new IllegalStateException("이미 신고한 게시물입니다.");
		}

		// 1. 신고자(Member) 엔티티 조회
		// DTO의 memberNo를 사용하여 DB에서 실제 회원 객체를 가져옵니다.
//        Member reporter = memberRepository.findById(reportDTO.getMemberNo())
//                .orElseThrow(() -> new IllegalArgumentException("신고자를 찾을 수 없습니다."));

		// 🛠 수정된 부분: builder로 가짜를 만드는 대신, DB에서 실제 존재하는 멤버를 찾습니다.
		// memberRepository의 ID가 이메일이라면 findById를, 아니라면 findByEmail(별도 생성 필요)을 사용하세요.
		Member member = memberRepository.findById(reportDTO.getMemberEmail())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다: " + reportDTO.getMemberEmail()));

		Report report = Report.builder().reporter(member) // 신고자 (Member 객체)
				.targetMemberId(reportDTO.getTargetMemberId()) // 신고대상자 (String ID)
				.targetType(reportDTO.getTargetType()) // "코멘트", "게시판", "채팅"
				.reportType(reportDTO.getReportType()) // "욕설", "사기", "기타" 등
				.reason(reportDTO.getReason()) // "기타"일 때만 내용이 있고 나머진 null일 수 있음
				.status(0) // 초기상태 : 접수됨(0)
				.targetNo(reportDTO.getTargetNo()) // 신고된 게시글이나 코멘트의 no
				.build();

		// 3. 이미지 파일 이름 처리
		// ReportDTO의 uploadFileNames 리스트를 순회하며 엔티티에 추가합니다.
		List<String> uploadFileNames = reportDTO.getUploadFileNames();

		if (uploadFileNames != null && !uploadFileNames.isEmpty()) {
			uploadFileNames.forEach(fileName -> {
				report.addImageString(fileName);
			});
		}

		// 4. DB 저장
		Report savedReport = reportRepository.save(report);

		log.info("신고 등록 완료. 생성된 신고 번호: {}", savedReport.getReportId());

		return savedReport.getReportId();
	}

	@Override
	public PageResponseDTO<ReportDTO> list(PageRequestDTO pageRequestDTO) {

		log.info("신고 목록 조회 기능 호출됨: " + pageRequestDTO);

		// 1. 페이징 정보 생성 (Pageable)
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 페이지 번호는 0부터 시작하므로 -1
				pageRequestDTO.getSize(), Sort.by("reportId").descending() // 최신 신고가 위로 오도록 정렬
		);

		// 2. DB에서 페이징된 Report 엔티티 목록 조회
		Page<Report> result = reportRepository.findAll(pageable);

		// 3. 엔티티 리스트를 DTO 리스트로 변환
		List<ReportDTO> dtoList = result.getContent().stream().map(report -> {
			ReportDTO dto = ReportDTO.builder().reportId(report.getReportId())
					.memberEmail(report.getReporter().getEmail()) // 신고자 이메일
					.targetMemberId(report.getTargetMemberId()).targetType(report.getTargetType())
					.reportType(report.getReportType()).reason(report.getReason()).status(report.getStatus())
					.targetNo(report.getTargetNo()).regDate(report.getRegDate()) // 등록일자 (Entity에 BaseEntity가 있다면)
					.build();

			// 이미지 파일 이름들도 DTO에 담아줍니다.
			List<String> fileNameList = report.getReportImage().stream().map(img -> img.getFileName())
					.collect(Collectors.toList());

			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());

		// 4. PageResponseDTO 구성하여 반환
		return PageResponseDTO.<ReportDTO>withAll().dtoList(dtoList).pageRequestDTO(pageRequestDTO)
				.totalCount(result.getTotalElements()) // 전체 데이터 개수
				.build();
	}

	@Override
	public ReportDTO read(Long reportId) {
		// 1. DB에서 엔티티 조회
		Report report = reportRepository.findById(reportId)
				.orElseThrow(() -> new IllegalArgumentException("해당 신고 내역이 존재하지 않습니다."));

		// 2. 엔티티 -> DTO 변환
		ReportDTO dto = ReportDTO.builder().reportId(report.getReportId()).memberEmail(report.getReporter().getEmail())
				.targetMemberId(report.getTargetMemberId()).targetType(report.getTargetType())
				.reportType(report.getReportType()).reason(report.getReason()).status(report.getStatus())
				.targetNo(report.getTargetNo()).regDate(report.getRegDate()).build();

		// 3. 이미지 파일명 리스트 처리
		List<String> fileNameList = report.getReportImage().stream().map(img -> img.getFileName())
				.collect(java.util.stream.Collectors.toList());

		dto.setUploadFileNames(fileNameList);

		return dto;
	}
}