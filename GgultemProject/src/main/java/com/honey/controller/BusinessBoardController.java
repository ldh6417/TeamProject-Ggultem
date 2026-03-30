package com.honey.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping("/admin/businessboard")
public class BusinessBoardController {
	
	private final BusinessBoardService businessBoardService;
	private final CustomFileUtil fileUtil;
	
	@PostMapping("/register")
	public Map<String, Long> register(BusinessBoardDTO businessBoardDTO) {
		
		List<MultipartFile> files = businessBoardDTO.getFiles();

		List<String> uploadFileNames = fileUtil.saveFiles(files);

		businessBoardDTO.setUploadFileNames(uploadFileNames);
		
		Long no = businessBoardService.register(businessBoardDTO);
		
		return Map.of("NO", no);
	}
	
	@GetMapping("/{no}")
	public BusinessBoardDTO getBusinessBoard(@PathVariable(name = "no") Long no) {
		return businessBoardService.get(no);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<BusinessBoardDTO> list(SearchDTO searchDTO) {
		
		if("all".equals(searchDTO.getSign())) {
			searchDTO.setSign(null);
		}
		
		if("all".equals(searchDTO.getCategory())) {
			searchDTO.setCategory(null);
		}
		return businessBoardService.adminList(searchDTO);
	}
	
	@GetMapping("/deletelist")
	public PageResponseDTO<BusinessBoardDTO> deleteList(SearchDTO searchDTO) {
		if("all".equals(searchDTO.getSign())) {
			searchDTO.setSign(null);
		}
		
		if("all".equals(searchDTO.getCategory())) {
			searchDTO.setCategory(null);
		}
		return businessBoardService.adminDeleteList(searchDTO);
	}
	
	@GetMapping("/approve/{no}") // 비즈니스 회원 게시판 승인로직
	public Map<String, String> approve(@PathVariable(name = "no") Long no) {
		
		businessBoardService.approve(no);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/reject/{no}") // 비즈니스 회원 게시판 승인로직
	public Map<String, String> reject(@PathVariable(name = "no") Long no) {
		
		businessBoardService.reject(no);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@PutMapping("/modify/{no}")
	public Map<String, String> modify(@PathVariable(name = "no") Long no, BusinessBoardDTO businessBoardDTO) {
		businessBoardDTO.setNo(no);
		BusinessBoardDTO oldBusinessBoardDTO = businessBoardService.get(no);
		
		businessBoardService.modify(businessBoardDTO, oldBusinessBoardDTO);
		
		return Map.of("RESULT", "SUCCESS");
	}
	
	@GetMapping("/delete/{no}")
	public Map<String, String> remove(@PathVariable(name = "no") Long no) {
		businessBoardService.remove(no);
		
		return Map.of("RESULT", "SUCCESS");
	}
	

	
}
