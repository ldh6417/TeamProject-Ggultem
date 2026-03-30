package com.honey.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.honey.dto.BusinessBoardDTO;
import com.honey.dto.BusinessStatsDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.BusinessBoardService;
import com.honey.util.CustomFileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/business/board")
public class BusinessBoardPageController {
	
	private final BusinessBoardService businessBoardService;
	private final CustomFileUtil fileUtil;
	
	@PostMapping("/register")
	public Map<String, Long> register(BusinessBoardDTO businessBoardDTO) {
		
		log.info("입력된 데이터: " + businessBoardDTO);
		
		List<MultipartFile> files = businessBoardDTO.getFiles();

		List<String> uploadFileNames = fileUtil.saveFiles(files);

		businessBoardDTO.setUploadFileNames(uploadFileNames);
		
		return Map.of("RESULT", businessBoardService.register(businessBoardDTO));
	}
	
	@GetMapping("/{no}")
	public BusinessBoardDTO getBusinessBoard(@PathVariable(name = "no") Long no) {
		return businessBoardService.get(no);
	}
	
	@GetMapping("/list/{email}")
	public PageResponseDTO<BusinessBoardDTO> list(SearchDTO searchDTO, @PathVariable(name = "email") String email) {
		
		if("all".equals(searchDTO.getSign())) {
			searchDTO.setSign(null);
		}
		
		if("all".equals(searchDTO.getCategory())) {
			searchDTO.setCategory(null);
		}
		
		return businessBoardService.list(searchDTO, email);
	}
	
	@GetMapping("/deletelist/{email}")
	public PageResponseDTO<BusinessBoardDTO> deleteList(SearchDTO searchDTO, @PathVariable(name = "email") String email) {
		if("all".equals(searchDTO.getSign())) {
			searchDTO.setSign(null);
		}
		
		if("all".equals(searchDTO.getCategory())) {
			searchDTO.setCategory(null);
		}
		return businessBoardService.deleteList(searchDTO, email);
	}
	
	@GetMapping("/adlist")
	public List<BusinessBoardDTO> adlist() {
		return businessBoardService.adPSlist();
	}
	
	@GetMapping("/adpllist")
	public List<BusinessBoardDTO> adPlList() {
		return businessBoardService.adPlList();
	}
	
	@PutMapping("/viewcount/{no}/{email}")
	public Map<String, String> viewCountAdd(@PathVariable(name = "no") Long no,
			@PathVariable(name="email") String email) {
		log.info("화면에서 넘어온 no 값 : " + no);
		businessBoardService.viewCountAdd(no, email);
		return Map.of("RESULT", "SUCCESS");
	}
	
	@PutMapping("/modify/{no}")
	public Map<String, String> modify(@PathVariable(name = "no") Long no, BusinessBoardDTO businessBoardDTO) {
		businessBoardDTO.setNo(no);
		BusinessBoardDTO oldBusinessBoardDTO = businessBoardService.get(no);
		
		businessBoardService.modify(businessBoardDTO, oldBusinessBoardDTO);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/remove/{no}")
	public Map<String, String> remove(@PathVariable(name = "no") Long no) {
		businessBoardService.remove(no);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/view/{fileName}")
	public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) {
		return fileUtil.getFile(fileName);
	}
	
	//상품관리 데이터 통계 처리를 위한 로직
	@GetMapping("/stats") // 경로에서 {email} 제거
	public BusinessStatsDTO getStats(@RequestParam("email") String email,
			@RequestParam("start") String start,
			@RequestParam("end") String end) {
		log.info("조회 기간: " + start + " ~ " + end);
	    return businessBoardService.getStats(email, start, end);
	}
	
}
