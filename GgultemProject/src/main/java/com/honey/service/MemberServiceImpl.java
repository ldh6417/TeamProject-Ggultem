package com.honey.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.honey.domain.Member;
import com.honey.dto.MemberDTO;
import com.honey.dto.PageRequestDTO;
import com.honey.dto.PageResponseDTO;
import com.honey.repository.MemberRepository;

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
	
	@Override
	public MemberDTO get(Long no) {
		java.util.Optional<Member> result = memberRepository.findById(no);
		Member member = result.orElseThrow();

		MemberDTO memberDTO = modelMapper.map(member, MemberDTO.class);
		return memberDTO;
	}

	@Override
	public Long register(MemberDTO memberDTO) {
		Member member = modelMapper.map(memberDTO, Member.class);
		
		member.changeStatus(1);
		member.addRole("ROLE_MEMBER");
		
		Member savedMember = memberRepository.save(member);

		return savedMember.getNo();
	}

	@Override
	public void modify(MemberDTO memberDTO) {
		Optional<Member> result = memberRepository.findById(memberDTO.getNo());
		Member member = result.orElseThrow();

		member.changePw(memberDTO.getPw());
		member.changePhone(memberDTO.getPhone());
		member.changeEmail(memberDTO.getEmail());
		member.changeNickName(memberDTO.getNickName());
		member.setEnabled(memberDTO.getEnabled());
		
		// 중요: enabled 값이 변경되었다면 changeStatus를 호출하도록 수정
	    if (member.getEnabled() != memberDTO.getEnabled()) {
	        member.changeStatus(memberDTO.getEnabled());
	    }
		
		memberRepository.save(member);
	}

	@Override
	public void remove(Long no) {
		Optional<Member> result = memberRepository.findById(no);
		Member member = result.orElseThrow();
		
		member.changeStatus(0);
		
		memberRepository.save(member);
	}

	@Override
	public PageResponseDTO<MemberDTO> list(PageRequestDTO pageRequestDTO) {
		Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1, // 1 페이지가 0 이므로 주의
				pageRequestDTO.getSize(), Sort.by("no").descending());
		//1페이지에 해당되는 레코드 10개를 가져온다.
		Page<Member> result = memberRepository.findAll(pageable);
		//1페이지에 해당되는 10개 레코드를 가져온다.
		List<MemberDTO> dtoList = result.getContent().stream().map(member -> modelMapper.map(member, MemberDTO.class))
				.collect(Collectors.toList());
		//전체 레코드수를 구함
		long totalCount = result.getTotalElements();
		
		PageResponseDTO<MemberDTO> responseDTO = PageResponseDTO.<MemberDTO>withAll().dtoList(dtoList)
				.pageRequestDTO(pageRequestDTO).totalCount(totalCount).build();
		
		return responseDTO;
	}

}
