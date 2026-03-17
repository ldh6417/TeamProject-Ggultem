package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.domain.MemberRole;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;



@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/admin/member")
public class MemberController {
	
	private final MemberService service;
	
	@GetMapping("/{email}")
	public MemberDTO getMember(@PathVariable(name = "email") String email) {
		return service.get(email);
	}
	
	@PostMapping("/")
	public Map<String, String> register(MemberDTO memberDTO) {
		log.info("여기입니다 =-==================="+memberDTO.toString());
		String email = service.register(memberDTO);
		return Map.of("EAMIL", email);
	}
	
	@GetMapping("/list")
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		return service.list(searchDTO);
	}
	
	@PutMapping("/{email}")
	public Map<String, String> modify(@PathVariable(name = "email") String email, MemberDTO memberDTO) {
		log.info("수정 요청 데이터: " + memberDTO);
		memberDTO.setEmail(email);
		service.modify(memberDTO);
		return Map.of("RESULT", "SUCCESS");
	}

	@PutMapping("/remove/{email}")
	public Map<String, String> remove(@PathVariable(name = "email") String email) {
		service.remove(email);
		return Map.of("RESULT", "SUCCESS");
	}
	
	
	
	
}
