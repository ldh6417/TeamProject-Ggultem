package com.honey.service;

import java.time.LocalDateTime;
import java.util.List;
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
import com.honey.domain.Member;
import com.honey.dto.BusinessBoardDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.BusinessBoardRepository;
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
	private final CustomFileUtil fileUtil;

	// 1. 등록(register) 메서드 수정
	@Override
	public Long register(BusinessBoardDTO businessBoardDTO) {
		Member member = memberRepository.findById(businessBoardDTO.getEmail())
				.orElseThrow(() -> new RuntimeException("작성자 정보를 찾을 수 없습니다."));

		BusinessBoard businessBoard = BusinessBoard.builder().title(businessBoardDTO.getTitle())
				.content(businessBoardDTO.getContent()).price(businessBoardDTO.getPrice())
				.category(businessBoardDTO.getCategory()).writer(businessBoardDTO.getWriter())
				.moveUrl(businessBoardDTO.getMoveUrl()).viewCount(0).member(member) // 연관 관계 직접 세팅
				.enabled(1).sign(false).build();

		if (businessBoardDTO.getEndDate() != null && !businessBoardDTO.getEndDate().isEmpty()) {
			try {
				LocalDateTime endLDT = java.time.LocalDate.parse(businessBoardDTO.getEndDate()).atStartOfDay();
				businessBoard.setEndDate(endLDT);
			} catch (Exception e) {
				log.error("날짜 변환 실패: " + e.getMessage());
			}
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
	public PageResponseDTO<BusinessBoardDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, searchDTO.getSize(),
				Sort.by("endDate").descending());

		Page<BusinessBoard> result = null;
		if (searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {

			if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionAllFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), Boolean.parseBoolean(searchDTO.getCategory()),
						pageable);

			} else if (searchDTO.getSign() != null) {
				result = boardRepository.searchByConditionSignFilter(searchDTO.getSearchType(), searchDTO.getKeyword(),
						Boolean.parseBoolean(searchDTO.getSign()), pageable);

			} else if (searchDTO.getCategory() != null) {
				result = boardRepository.searchByConditionCategoryFilter(searchDTO.getSearchType(),
						searchDTO.getKeyword(), Boolean.parseBoolean(searchDTO.getCategory()), pageable);
			}
		} else if (searchDTO.getSign() != null && searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessAllFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()),
					Boolean.parseBoolean(searchDTO.getCategory()));

		} else if (searchDTO.getSign() != null) {
			result = boardRepository.findAllBusinessSignFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()));
		} else if (searchDTO.getCategory() != null) {
			result = boardRepository.findAllBusinessCategoryFilter(pageable, Boolean.parseBoolean(searchDTO.getSign()));
		} else {
			result = boardRepository.findAllBusiness(pageable);
		}

		List<BusinessBoardDTO> dtoList = result.getContent().stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}

			// LocalDateTime -> String 변환 (리스트에서 날짜를 보여줘야 한다면)
			if (businessBoard.getEndDate() != null) {
				dto.setEndDate(businessBoard.getEndDate().toLocalDate().toString());
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
	public BusinessBoardDTO get(Long no) {
		Optional<BusinessBoard> result = boardRepository.findById(no);
		BusinessBoard businessBoard = result.orElseThrow();

		BusinessBoardDTO businessBoardDTO = modelMapper.map(businessBoard, BusinessBoardDTO.class);

		if (businessBoard.getMember() != null) {
			businessBoardDTO.setEmail(businessBoard.getMember().getEmail());
		}

		if (businessBoard.getEndDate() != null) {
			businessBoardDTO.setEndDate(businessBoard.getEndDate().toLocalDate().toString());
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
		if (files != null && !files.get(0).isEmpty()) {
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
		businessBoard.changeCategory(businessBoardDTO.getCategory());
		// 2. [핵심] 타입이 다른 endDate를 지훈님이 직접 수동으로 세팅하세요!
		if (businessBoardDTO.getEndDate() != null && !businessBoardDTO.getEndDate().isEmpty()) {
			try {
				// String "2026-06-30" -> LocalDateTime 변환
				String dateStr = businessBoardDTO.getEndDate();
				// LocalDate로 파싱 후 시간(00:00:00) 추가
				LocalDateTime endLDT = java.time.LocalDate.parse(dateStr).atStartOfDay();
				businessBoard.setEndDate(endLDT);
			} catch (Exception e) {
				log.error("날짜 변환 실패: " + e.getMessage());
			}
		}

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
		List<BusinessBoard> result = boardRepository.findADPSList(category, sign);

		List<BusinessBoardDTO> dtoList = result.stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}

			// LocalDateTime -> String 변환 (리스트에서 날짜를 보여줘야 한다면)
			if (businessBoard.getEndDate() != null) {
				dto.setEndDate(businessBoard.getEndDate().toLocalDate().toString());
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
	public void viewCountAdd(Long no) {
		BusinessBoard businessBoard = boardRepository.findById(no).orElseThrow();
		businessBoard.setViewCount(businessBoard.getViewCount() + 1);

		boardRepository.save(businessBoard);
	}

	@Override
	public List<BusinessBoardDTO> adPlList() {
		String category = "powerlink";
		boolean sign = true;
		List<BusinessBoard> result = boardRepository.findADPSList(category, sign);

		List<BusinessBoardDTO> dtoList = result.stream().map(businessBoard -> {
			BusinessBoardDTO dto = modelMapper.map(businessBoard, BusinessBoardDTO.class);

			// 🚩 이메일 및 날짜 수동 매핑 (타입이 달라 ModelMapper가 놓친 부분)
			if (businessBoard.getMember() != null) {
				dto.setEmail(businessBoard.getMember().getEmail());
			}

			// LocalDateTime -> String 변환 (리스트에서 날짜를 보여줘야 한다면)
			if (businessBoard.getEndDate() != null) {
				dto.setEndDate(businessBoard.getEndDate().toLocalDate().toString());
			}

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
}
