package com.honey.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.honey.domain.BusinessBoard;
import com.honey.domain.ClickLog;
import com.honey.domain.Member;
import com.honey.dto.BusinessBoardDTO;
import com.honey.dto.BusinessStatsDTO;
import com.honey.dto.DailyStat;
import com.honey.dto.DailyStatInterface;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BusinessBoardRepository;
import com.honey.repository.ClickLogRepository;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class BusinessBoardServiceImpl implements BusinessBoardService {

	private final ModelMapper modelMapper;
	private final BusinessBoardRepository boardRepository;
	private final MemberRepository memberRepository;
	private final ClickLogRepository clickLogRepository;
	private final CustomFileUtil fileUtil;

	// 1. 등록(register) 메서드 수정
	@Override
	public Long register(BusinessBoardDTO businessBoardDTO) {
		Member member = memberRepository.findById(businessBoardDTO.getEmail())
				.orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));

		BusinessBoard businessBoard = BusinessBoard.builder().title(businessBoardDTO.getTitle())
				.content(businessBoardDTO.getContent()).price(businessBoardDTO.getPrice())
				.category(businessBoardDTO.getCategory()).writer(businessBoardDTO.getWriter())
				.moveUrl(businessBoardDTO.getMoveUrl()).viewCount(0).member(member).endDate(businessBoardDTO.getEndDate()) // 연관 관계 직접 세팅
				.enabled(1).sign(false).onOff(false).build();
		
		if(LocalDateTime.now().isBefore(businessBoardDTO.getEndDate())) {
			businessBoard.setOnOff(true);
		}

		// 파일 처리
		List<String> newFileNames = businessBoardDTO.getUploadFileNames();
		if (newFileNames != null && !newFileNames.isEmpty()) {
			businessBoard.clearList(); // 초기화 후 추가
			newFileNames.forEach(businessBoard::addImageString);
		}

		return boardRepository.save(businessBoard).getNo();
	}

	// 2. 리스트(list) 메서드 수정
	@Override
	@Transactional(readOnly = true)
	public PageResponseDTO<BusinessBoardDTO> list(SearchDTO searchDTO, String email) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("endDate").descending());
		
		log.info("넘어온 이메일 정보 : "+email);

		Page<BusinessBoard> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionAllFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), searchDTO.getCategory(),
						pageable, email);

			} else if (searchDTO.getSign() != null) {
				result = boardRepository.searchByConditionSignFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), pageable, email);

			} else if (searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionCategoryFilter(searchDTO.getSearchType(),
						searchDTO.getKeyword(), searchDTO.getCategory(), pageable, email);
			}
		} else if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessAllFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()),
					searchDTO.getCategory(), email);

		} else if (searchDTO.getSign() != null) {
			result = boardRepository.findAllBusinessSignFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()), email);
		} else if (searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessCategoryFilter(pageable, searchDTO.getCategory(), email);
		} else {
			result = boardRepository.findAllBusiness(pageable, email);
		}

		List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}
			
			if(LocalDateTime.now().isBefore(businessBoard.getEndDate())) {
				dto.setOnOff(true);
			} else {
				dto.setOnOff(false);
			}

			List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
					.collect(Collectors.toList());
			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());

		return PageResponseDTO.<BusinessBoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}
	
	@Override
	@Transactional(readOnly = true)
	public PageResponseDTO<BusinessBoardDTO> deleteList(SearchDTO searchDTO, String email) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("dtdDate").descending());
		
		log.info("넘어온 이메일 정보 : "+email);

		Page<BusinessBoard> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionDeleteFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), searchDTO.getCategory(),
						pageable, email);

			} else if (searchDTO.getSign() != null) {
				result = boardRepository.searchByConditionDeleteSignFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), pageable, email);

			} else if (searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionDeleteCategoryFilter(searchDTO.getSearchType(),
						searchDTO.getKeyword(), searchDTO.getCategory(), pageable, email);
			}
		} else if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessDeleteFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()),
					searchDTO.getCategory(), email);

		} else if (searchDTO.getSign() != null) {
			result = boardRepository.findAllBusinessDeleteSignFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()), email);
		} else if (searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessDeleteCategoryFilter(pageable, searchDTO.getCategory(), email);
		} else {
			result = boardRepository.findDeleteBusiness(pageable, email);
		}

		List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}
			
			dto.setOnOff(false);

			List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
					.collect(Collectors.toList());
			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());

		return PageResponseDTO.<BusinessBoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
				.totalCount(result.getTotalElements()).build();
	}
	
	////////////////////////////////
	/// Admin List Service
	/// ////////////////////////////
		@Override
		@Transactional(readOnly = true)
		public PageResponseDTO<BusinessBoardDTO> adminList(SearchDTO searchDTO) {
			Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
					Sort.by("endDate").descending());
			
			Page<BusinessBoard> result = null;
			if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

				if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
					result = boardRepository.adminSearchByConditionAllFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
							Boolean.parseBoolean(searchDTO.getSign()), searchDTO.getCategory(),
							pageable);

				} else if (searchDTO.getSign() != null) {
					result = boardRepository.adminSearchByConditionSignFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
							Boolean.parseBoolean(searchDTO.getSign()), pageable);

				} else if (searchDTO.getCategory() != null) {
					result = boardRepository.adminSearchByConditionCategoryFilter(searchDTO.getSearchType(),
							searchDTO.getKeyword(), searchDTO.getCategory(), pageable);
				}
			} else if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
				result = boardRepository.adminFindAllBusinessAllFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()),
						searchDTO.getCategory());

			} else if (searchDTO.getSign() != null) {
				result = boardRepository.adminFindAllBusinessSignFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()));
			} else if (searchDTO.getCategory() != null) {
				result = boardRepository.adminFindAllBusinessCategoryFilter(pageable, searchDTO.getCategory());
			} else {
				result = boardRepository.adminFindAllBusiness(pageable);
			}

			List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
				BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

				// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
				if (businessBoard.getMember() != null) {
					dto.setEmail(businessBoard.getMember().getEmail());
				}
				
				if(LocalDateTime.now().isBefore(businessBoard.getEndDate())) {
					dto.setOnOff(true);
				} else {
					dto.setOnOff(false);
				}

				List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
						.collect(Collectors.toList());
				dto.setUploadFileNames(fileNameList);

				return dto;
			}).collect(Collectors.toList());

			return PageResponseDTO.<BusinessBoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
					.totalCount(result.getTotalElements()).build();
		}
		
		@Override
		@Transactional(readOnly = true)
		public PageResponseDTO<BusinessBoardDTO> adminDeleteList(SearchDTO searchDTO) {
			Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
					Sort.by("dtdDate").descending());
			
			Page<BusinessBoard> result = null;
			if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

				if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
					result = boardRepository.adminSearchByConditionDeleteFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
							Boolean.parseBoolean(searchDTO.getSign()), searchDTO.getCategory(),
							pageable);

				} else if (searchDTO.getSign() != null) {
					result = boardRepository.adminSearchByConditionDeleteSignFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
							Boolean.parseBoolean(searchDTO.getSign()), pageable);

				} else if (searchDTO.getCategory() != null) {
					result = boardRepository.adminSearchByConditionDeleteCategoryFilter(searchDTO.getSearchType(),
							searchDTO.getKeyword(), searchDTO.getCategory(), pageable);
				}
			} else if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
				result = boardRepository.adminFindAllBusinessDeleteFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()),
						searchDTO.getCategory());

			} else if (searchDTO.getSign() != null) {
				result = boardRepository.adminFindAllBusinessDeleteSignFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()));
			} else if (searchDTO.getCategory() != null) {
				result = boardRepository.adminFindAllBusinessDeleteCategoryFilter(pageable, searchDTO.getCategory());
			} else {
				result = boardRepository.adminFindDeleteBusiness(pageable);
			}

			List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
				BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

				// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
				if (businessBoard.getMember() != null) {
					dto.setEmail(businessBoard.getMember().getEmail());
				}
				
				dto.setOnOff(false);

				List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
						.collect(Collectors.toList());
				dto.setUploadFileNames(fileNameList);

				return dto;
			}).collect(Collectors.toList());

			return PageResponseDTO.<BusinessBoardDTO>withAll().dtoList(dtoList).pageRequestDTO(searchDTO)
					.totalCount(result.getTotalElements()).build();
		}

	@Override
	@Transactional(readOnly = true)
	public BusinessBoardDTO get(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();

		BusinessBoardDTO businessBoardDTO = modelMapper.map(businessBoard, BusinessBoardDTO.class);

		if (businessBoard.getMember() != null) {
			businessBoardDTO.setEmail(businessBoard.getMember().getEmail());
		}
		
		if(LocalDateTime.now().isBefore(businessBoard.getEndDate())) {
			businessBoardDTO.setOnOff(true);
		} else {
			businessBoardDTO.setOnOff(false);
		}

		List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			businessBoardDTO.setUploadFileNames(fileNameList);
		}

		return businessBoardDTO;
	}

	@Override
	public void approve(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();

		businessBoard.changeSign(true);

		boardRepository.save(businessBoard);
	}
	
	@Override
	public void reject(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();
		
		businessBoard.changeSign(false);
		
		boardRepository.save(businessBoard);
	}

	@Override
	public void modify(BusinessBoardDTO businessBoardDTO, BusinessBoardDTO oldBusinessBoardDTO) {

		List<String> oldFileNames = oldBusinessBoardDTO.getUploadFileNames();

		List<MultipartFile> files = businessBoardDTO.getFiles();

		List<String> currentUpdateFileNames = null;
		if (files != null && !files.isEmpty()) {
			currentUpdateFileNames = fileUtil.saveFiles(files);
		}

		List<String> uploadFileNames = businessBoardDTO.getUploadFileNames();

		if (currentUpdateFileNames != null && !currentUpdateFileNames.isEmpty()) {
			uploadFileNames.addAll(currentUpdateFileNames);
		}

		businessBoardDTO.setUploadFileNames(uploadFileNames);

		BusinessBoard businessBoard = boardRepository.findById(businessBoardDTO.getNo()).orElseThrow();

		businessBoard.clearList();

		List<String> newFileNames = businessBoardDTO.getUploadFileNames();
		if (newFileNames != null && !newFileNames.isEmpty()) {
			newFileNames.forEach(fileName -> {
				businessBoard.addImageString(fileName);
			});
		}

		businessBoard.changeTitle(businessBoardDTO.getTitle());
		businessBoard.changePrice(businessBoardDTO.getPrice());
		businessBoard.changeContent(businessBoardDTO.getContent());
		businessBoard.changeMoveUrl(businessBoardDTO.getMoveUrl());
		businessBoard.setEndDate(businessBoardDTO.getEndDate());
		
		businessBoard.changeSign(false); // 광고 수정시 관리자의 승인이 필요

		boardRepository.save(businessBoard);

		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			List<String> removeFiles = oldFileNames.stream().filter(fileName -> uploadFileNames.indexOf(fileName) == -1)
					.collect(Collectors.toList());
			fileUtil.deleteFiles(removeFiles);
		}
	}

	@Override
	public void remove(Long no) {
		BusinessBoard businessBoard = boardRepository.findById(no).orElseThrow();

		businessBoard.changeEnabled(0);

		List<String> oldFileNames = businessBoard.getBItemList().stream().map(item -> item.getFileName())
				.collect(Collectors.toList());

		if (oldFileNames != null && !oldFileNames.isEmpty()) {
			fileUtil.deleteFiles(oldFileNames);
		}

		businessBoard.clearList();

		boardRepository.save(businessBoard);
	}

	@Override
	public List<BusinessBoardDTO> adPSlist() {
		String category = "powershoping";
		boolean sign = true;
		long minBizMoney = 100;
		List<BusinessBoard> result = boardRepository.findADPSList(category, sign, minBizMoney);

		List<BusinessBoardDTO> dtoList = result.stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}

			List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
					.collect(Collectors.toList());
			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());

		// --- 🚩 랜덤 5개 추출 로직 추가 ---
		if (dtoList.size() > 5) {
			java.util.Collections.shuffle(dtoList); // 리스트를 무작위로 섞기
			return dtoList.subList(0, 5); // 0번부터 4번까지 5개만 잘라서 반환
		}

		return dtoList;
	}

	@Override
	public void viewCountAdd(Long no, String email) {
		BusinessBoard businessBoard = boardRepository.findById(no).orElseThrow();
		businessBoard.setViewCount(businessBoard.getViewCount() + 1);
		
		ClickLog log = ClickLog.builder()
	            .board(businessBoard)
	            .userEmail(email)
	            .build();
		
		clickLogRepository.save(log);

		boardRepository.save(businessBoard);
	}

	@Override
	public List<BusinessBoardDTO> adPlList() {
		String category = "powerlink";
		boolean sign = true;
		long minBizMoney = 100;
		List<BusinessBoard> result = boardRepository.findADPSList(category, sign, minBizMoney);

		List<BusinessBoardDTO> dtoList = result.stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}

//			// LocalDateTime -> String 변환 (리스트에서 날짜를 보여줘야 한다면)
//			if (businessBoard.getEndDate() != null) {
//				dto.setEndDate(businessBoard.getEndDate().toLocalDate().toString());
//			}

			List<String> fileNameList = businessBoard.getBItemList().stream().map(item -> item.getFileName())
					.collect(Collectors.toList());
			dto.setUploadFileNames(fileNameList);

			return dto;
		}).collect(Collectors.toList());

		// --- 🚩 랜덤 5개 추출 로직 추가 ---
		if (dtoList.size() > 3) {
			java.util.Collections.shuffle(dtoList); // 리스트를 무작위로 섞기
			return dtoList.subList(0, 3); // 0번부터 4번까지 5개만 잘라서 반환
		}

		return dtoList;
	}
	
	
	// 광고 상품 데이터 처리를 위한 로직
	@Override
	public BusinessStatsDTO getStats(String email, String start, String end) {
	    // 1. 파워링크 통계
	    long plClicks = boardRepository.sumViewCountByCategory(email, "powerlink");
	    long plCount = boardRepository.countByCategory(email, "powerlink");

	    // 2. 파워쇼핑 통계
	    long psClicks = boardRepository.sumViewCountByCategory(email, "powershoping");
	    long psCount = boardRepository.countByCategory(email, "powershoping");

	    LocalDate startDate = LocalDate.parse(start);
	    LocalDate endDate = LocalDate.parse(end);
	    
	    // 1. DB에서 실제 데이터 가져오기 (기존 로직)
	    List<DailyStatInterface> dbResults = boardRepository.getDailyStats(
	            email, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));

	    // 2. DB 데이터를 Map에 담기 (날짜를 Key로 해서 찾기 쉽게)
	    Map<String, DailyStatInterface> resultMap = dbResults.stream()
	            .collect(Collectors.toMap(DailyStatInterface::getDay, r -> r));

	    // 3. 시작일부터 종료일까지 모든 날짜를 순회하며 리스트 생성
	    List<DailyStat> fullPeriodStats = new ArrayList<>();
	    
	    // startDate부터 endDate까지 1일씩 증가하며 반복 (endDate 포함을 위해 plusDays(1))
	    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
	        String dateStr = date.toString(); // "2026-03-23" 형태
	        
	        if (resultMap.containsKey(dateStr)) {
	            // DB에 데이터가 있는 날짜면 그 데이터를 넣고
	            DailyStatInterface dbData = resultMap.get(dateStr);
	            fullPeriodStats.add(new DailyStat(dbData));
	        } else {
	            // 데이터가 없는 날짜면 0으로 채운 빈 객체를 생성해서 넣음
	            fullPeriodStats.add(new DailyStat(dateStr, 0L, 0L, 0L));
	        }
	    }

	    return BusinessStatsDTO.builder()
	            .totalPowerLinkClicks(plClicks)
	            .totalPowerLinkCount(plCount)
	            .totalPowerShoppingClicks(psClicks)
	            .totalPowerShoppingCount(psCount)
	            .dailyStats(fullPeriodStats)
	            .build();
	}
	
	
	
	
	
}
