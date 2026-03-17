package com.honey.security;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.honey.domain.Member;
import com.honey.dto.MemberDTO;
import com.honey.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.getWithRoles(username); 
		  if (member == null) { 
		   throw new UsernameNotFoundException("Not Found"); 
		  } 
		  MemberDTO memberDTO =  
		    new MemberDTO(member.getEmail(), member.getPw(), member.getNickname(),  
		      member.isSocial(), 
		member.getMemberRoleSet().stream().map(memberRole -> memberRole.name()).collect(Collectors.toSet()),member.getRegDate()); 
		  return memberDTO; 
	} 

}
