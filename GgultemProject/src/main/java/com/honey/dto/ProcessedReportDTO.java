package com.honey.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProcessedReportDTO {

    private Long processedReportId; // 처리 번호
    private Long reportId;          // 원본 신고 번호
    
    private String adminEmail;      // 관리자 이메일
    private String actionNote;      // 처리 내용 (조치 사유)
    private Integer memberStatus;   // 변경할 회원 상태 (2, 3, 4 등)
    private String reportStatus;    // 신고 처리 상태 (접수, 처리완료)

    // ⭐ 이 필드가 있어야 서비스의 빨간 줄이 사라집니다!
    private ReportDTO reportDTO;    

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDate;
}