package com.honey.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.google.gson.Gson;
import com.honey.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info(" ");
		log.info(authentication);
		log.info(" ");
		MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
		Map<String, Object> claims = memberDTO.getClaims();
		claims.put("accessToken", ""); // 나중에 구현
		claims.put("refreshToken", ""); // 나중에 구현

		Gson gson = new Gson();
		String jsonStr = gson.toJson(claims);

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter printWriter = response.getWriter();
		printWriter.println(jsonStr);
		printWriter.close();
	}
}
