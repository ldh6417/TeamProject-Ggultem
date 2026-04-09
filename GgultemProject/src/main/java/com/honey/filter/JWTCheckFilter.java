package com.honey.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.honey.dto.MemberDTO;
import com.honey.util.JWTUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("--------------------  JWTCheckFilter ------------------------------------------------------ ");
		String authHeaderStr = request.getHeader("Authorization");
		try {
			// Bearer accestoken ............... 토큰이 정상적이면 그대로 요구사항진행
			// Bearer[공백]accestoken... "Bearer " 접두사를 제외한 JWT 토큰만 추출
			String accessToken = authHeaderStr.substring(7);
			Map<String, Object> claims = JWTUtil.validateToken(accessToken);
			log.info("JWT claims: " + claims);

			// filterChain.doFilter(request, response); //이하 추가
			String email = (String) claims.get("email");
			String pw = (String) claims.get("pw");
			String nickname = (String) claims.get("nickname");
			Boolean social = (Boolean) claims.get("social");
			Set<String> roleNames = (Set<String>) claims.get("roleNames");
			LocalDateTime regDate = (LocalDateTime) claims.get("regDate");

			MemberDTO memberDTO = new MemberDTO(email, pw, nickname, social.booleanValue(), roleNames, regDate);

			// 스프링 시큐리티에서 인증 정보를 담는 객체
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDTO,
					pw, memberDTO.getAuthorities());

			// 이 객체를 SecurityContextHolder에 넣으면,
			// 해당 요청은 인증된 사용자로 처리됨
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			log.error("JWT Check Error .................................... ");
			log.error(e.getMessage());
			Gson gson = new Gson();
			String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
			response.setContentType("application/json");
			PrintWriter printWriter = response.getWriter();
			printWriter.println(msg);
			printWriter.close();
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    // 1. OPTIONS 요청(Preflight)은 무조건 패스 (CORS의 핵심!) 📍
	    if (request.getMethod().equals("OPTIONS")) {
	        return true;
	    }

	    String path = request.getRequestURI();
	    log.info("check uri: " + path);

	    // 2. 토큰 체크가 필요 없는 경로들 명시
	    if (path.startsWith("/member/kakao") || 
	        path.startsWith("/member/google") || 
	        path.startsWith("/api/member/login") ||
	        path.equals("/member/refresh") ||
	        path.startsWith("/ws")) { // 웹소켓
	        return true;
	    }

	    // 3. 이미지 조회 및 업로드 경로 예외 처리 (매우 중요! 🐝)
	    // BoardController의 /board/upload와 ItemBoard 관련 경로를 모두 열어줍니다.
	    if (path.startsWith("/board/view/") || 
	        path.startsWith("/board/upload") ||
	        path.startsWith("/itemBoard") || 
	        path.startsWith("/api/itemBoard")) {
	        return true;
	    }
	    
	    // 4. 메인 페이지 등 기본 경로
	    if (path.equals("/") || path.isEmpty()) {
	        return true;
	    }

	    return false;
	}

}
