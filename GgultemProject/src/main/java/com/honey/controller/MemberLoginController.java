package com.honey.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.honey.dto.MemberDTO;
import com.honey.service.MemberService;
import com.honey.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberLoginController {
	
	private final MemberService memberService;

    @GetMapping("/kakao")
    public Map<String, Object> getMemberFromKakao(String code) {
        log.info("카카오 인가 코드: " + code);
        
        MemberDTO memberDTO = memberService.getKakaoMember(code);
        
        Map<String, Object> claims = memberDTO.getClaims(); 
        String jwtAccessToken = JWTUtil.generateToken(claims, 10); 
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24); 
         
        claims.put("accessToken", jwtAccessToken); 
        claims.put("refreshToken", jwtRefreshToken);
        
        return claims;
    }
    
    @PostMapping("/google/accessToken")
    public Map<String, Object> googleLogin(@RequestBody Map<String, String> params) {
    	String authCode = params.get("code");
        // 서비스에서 구글 사용자 정보 처리 및 JWT 발급까지 한 번에!
        MemberDTO memberDTO = memberService.getMemberWithGoogle(authCode);
        
        Map<String, Object> claims = memberDTO.getClaims(); 
        String jwtAccessToken = JWTUtil.generateToken(claims, 10); 
        String jwtRefreshToken = JWTUtil.generateToken(claims, 60 * 24); 
         
        claims.put("accessToken", jwtAccessToken); 
        claims.put("refreshToken", jwtRefreshToken);

        return claims;
    }
}
