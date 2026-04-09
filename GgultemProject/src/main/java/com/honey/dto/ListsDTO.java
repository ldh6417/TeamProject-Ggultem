package com.honey.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListsDTO {
    private List<BoardDTO> latestCommunity; // 커뮤니티 최신 5개
    private List<NoticeDTO> latestNotice;   // 공지사항 최신 5개
    private List<ReportDTO> latestReports;  // 미처리 신고 최신 5개
    private List<BlackListDTO> blacklists;  // 블랙리스트 데이터

    // Key: enabled값(0~4), Value: 해당 유저 수
    private Map<Integer, Long> userStatusCounts;
}