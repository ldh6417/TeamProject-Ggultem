package com.honey.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.honey.domain.BlackList;
import com.honey.domain.Board;
import com.honey.domain.Member;
import com.honey.domain.Notice;
import com.honey.domain.Report;
import com.honey.dto.BlackListDTO;
import com.honey.dto.BoardDTO;
import com.honey.dto.ListsDTO;
import com.honey.dto.NoticeDTO;
import com.honey.dto.ReportDTO;
import com.honey.dto.StatsDTO;
import com.honey.repository.BizMoneyHistoryRepository;
import com.honey.repository.BlackListRepository;
import com.honey.repository.BoardRepository;
import com.honey.repository.BusinessBoardRepository;
import com.honey.repository.ItemBoardRepository;
import com.honey.repository.MemberRepository;
import com.honey.repository.NoticeRepository;
import com.honey.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
	
	private final MemberRepository memberRepository;
	private final ItemBoardRepository itemRepository;
	private final BusinessBoardRepository adRepository;
	private final BizMoneyHistoryRepository paymentRepository;
	private final BoardRepository boardRepository;
	private final NoticeRepository noticeRepository;
	private final ReportRepository reportRepository;
	private final BlackListRepository blacklistRepository;
	
	private final ModelMapper modelMapper;

	@Override
	public StatsDTO getStats() {
		LocalDateTime startOfToday = LocalDateTime.now();
		
	    return StatsDTO.builder()
	            .memberCount(memberRepository.count())
	            .itemCount(itemRepository.count())
	            .adCount(adRepository.countActiveAds(startOfToday)) // 활성 광고만 카운트
	            .totalCharge(paymentRepository.sumAllCharge()) // 전체 충전액 합계
	            .totalSales(paymentRepository.sumAllSpend())  // 전체 지출액 합계
	            .build();
	}

	@Override
	public ListsDTO getLists() {
	    // 유저 상태 통계 계산 🍯
	    Map<Integer, Long> statusCounts = memberRepository.findAll().stream()
	            .collect(Collectors.groupingBy(Member::getEnabled, Collectors.counting()));
	    
	    List<Board> boards = boardRepository.findTop5ByOrderByRegDateDesc();
	    List<Notice> notices = noticeRepository.findTop5ByOrderByRegDateDesc();
	    List<Report> reports = reportRepository.findTop5ByProcessedFalseOrderByRegDateDesc();
	    List<BlackList> blacklists = blacklistRepository.findRecentBlacklist();
	    
	    List<BoardDTO> boardDTOs = boards.stream()
	            .map(board -> modelMapper.map(board, BoardDTO.class))
	            .collect(Collectors.toList());
	            
	    List<NoticeDTO> noticeDTOs = notices.stream()
	            .map(notice -> modelMapper.map(notice, NoticeDTO.class))
	            .collect(Collectors.toList());
	    
	    List<ReportDTO> reportDTOs = reports.stream()
	            .map(report -> modelMapper.map(report, ReportDTO.class))
	            .collect(Collectors.toList());
	    
	    List<BlackListDTO> blacklistDTOs = blacklists.stream()
	            .map(blacklist -> modelMapper.map(blacklist, BlackListDTO.class))
	            .collect(Collectors.toList());

	    return ListsDTO.builder()
	            .latestCommunity(boardDTOs)
	            .latestNotice(noticeDTOs)
	            .latestReports(reportDTOs) // 미처리 신고만!
	            .blacklists(blacklistDTOs)
	            .userStatusCounts(statusCounts)
	            .build();
	}

}
