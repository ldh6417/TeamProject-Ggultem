package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.honey.domain.Member;
import com.honey.domain.MemberRole;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.dto.SearchDTO;
import com.honey.repository.MemberRepository;
import com.honey.util.CustomFileUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	private final SearchLogService searchLogSearvice;
	private final CustomFileUtil fileUtil;
	private final PasswordEncoder passwordEncoder; // 주입 필요

	@Override
	public MemberDTO get(String email) {
		Member member = memberRepository.findById(email).orElseThrow();

		MemberDTO memberDTO =  
			    new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(),  
			      member.isSocial(), 
			member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),member.getRegDate());

		List<String> fileNameList = member.getThumbnailList().stream().map(thumbnail -> thumbnail.getFileName())
				.collect(Collectors.toList());

		if (fileNameList != null && !fileNameList.isEmpty()) {
			memberDTO.setUploadFileNames(fileNameList);
		} else {
			memberDTO.setUploadFileNames(List.of("default.jpg"));
		}

		return memberDTO;
	}

	@Override
	public String register(MemberDTO memberDTO) {
		Member member = modelMapper.map(memberDTO, Member.class);
		
		member.changePw(passwordEncoder.encode(memberDTO.getPw())); // 암호화
		member.changeStatus(1);
		member.addRole(MemberRole.MEMBER);

		Member savedMember = memberRepository.save(member);

		return savedMember.getEmail();
	}

	@Override
	public void modify(MemberDTO memberDTO) {
		Member member = memberRepository.findById(memberDTO.getEmail()).orElseThrow();

		// 1. 비밀번호: 넘어온 값이 있을 때만 수정 (암호화는 필수!)
	    if (memberDTO.getPw() != null && !memberDTO.getPw().isEmpty()) {
	        // 비밀번호를 수정할 경우 반드시 암호화해서 넣어야 로그인이 됩니다.
	        member.changePw(passwordEncoder.encode(memberDTO.getPw())); 
	        log.info("비밀번호 변경됨");
	    } else {
	        log.info("비밀번호 변경 안 함 (기존 유지)");
	    }
		member.changePhone(memberDTO.getPhone());
		member.changeNickName(memberDTO.getNickname());
		
		log.info("수정된 데이터 =" + member.toString());
		
	    if (memberDTO.getEnabled() != null) {
	        if (!member.getEnabled().equals(memberDTO.getEnabled())) {
	            member.changeStatus(memberDTO.getEnabled());
	        }
	    }

		memberRepository.save(member);
	}

	@Override
	public void remove(String email) {
		Member member = memberRepository.findById(email).orElseThrow();

		member.changeStatus(0);
		
		List<String> oldFileNames = member.getThumbnailList().stream()
	            .map(thumbnail -> thumbnail.getFileName())
	            .collect(Collectors.toList());
	    
	    if (oldFileNames != null && !oldFileNames.isEmpty()) {
	        fileUtil.deleteFiles(oldFileNames);
	    }

	    member.clearList();

		memberRepository.save(member);
	}

	@Override
	public PageResponseDTO<MemberDTO> list(SearchDTO searchDTO) {
		Pageable pageable = PageRequest.of(searchDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				searchDTO.getSize(), Sort.by("regDate").descending());
		
		Page<Member> result = null;
		if(searchDTO.getKeyword() != null && !searchDTO.getKeyword().isEmpty()) {
			//searchLogSearvice.logSearch(searchDTO);
			result = memberRepository.searchByCondition(
					searchDTO.getSearchType(),
					searchDTO.getKeyword(),
					pageable);
		} else {
			result = memberRepository.findAll(pageable);
		}
		
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> {
	        MemberDTO dto = new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(), member.isSocial(), 
	        		member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()), member.getRegDate());
	        
	        
	        List<String> fileNameList = member.getThumbnailList().stream()
	                .map(thumbnail -> thumbnail.getFileName())
	                .collect(Collectors.toList());

	        if (fileNameList != null && !fileNameList.isEmpty()) {
	            dto.setUploadFileNames(fileNameList);
	        } else {
	            dto.setUploadFileNames(List.of("default.jpg"));
	        }

	        return dto;
	    }).collect(Collectors.toList());

	long totalCount = result.getTotalElements();

	PageResponseDTO<MemberDTO> responseDTO = PageResponseDTO.<MemberDTO>withAll().dtoList(dtoList)
			.pageRequestDTO(searchDTO).totalCount(totalCount).build();

	return responseDTO;
	}

	@Override
	public void updateToThumbnail(MemberDTO memberDTO) {
	    Member member = memberRepository.findById(memberDTO.getEmail()).orElseThrow();

	    member.clearList();
	    
	    log.info("저장된 이름 = "+memberDTO.getUploadFileNames().toString());
	    
	    List<String> newFileNames = memberDTO.getUploadFileNames();
	    if (newFileNames != null && !newFileNames.isEmpty()) {
	        newFileNames.forEach(fileName -> {
	            member.addImageString(fileName);
	        });
	    }

	    memberRepository.save(member);
	}
}
