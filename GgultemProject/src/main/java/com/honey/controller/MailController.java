package com.honey.controller;

import com.honey.service.MailService;
import com.honey.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;
    private final RedisUtil redisUtil;

    // 1. 인증번호 발송
    @PostMapping("/send")
    public Map<String, String> sendMail(@RequestParam("email") String email) {
        log.info("메일 발송 요청: " + email);
        mailService.sendMail(email); // 이 내부에서 redisUtil.setDataExpire 호출됨
        return Map.of("result", "SUCCESS");
    }

    // 2. 인증번호 검증
    @PostMapping("/verify")
    public Map<String, Object> verifyCode(@RequestParam("email") String email, 
                                          @RequestParam("code") String code) {
        log.info("인증 시도 - 이메일: {}, 입력코드: {}", email, code);
        
        String savedCode = redisUtil.getData(email);
        boolean isSuccess = savedCode != null && savedCode.equals(code);

        if (isSuccess) {
            redisUtil.deleteData(email); // 인증 성공 시 Redis 데이터 삭제 (깔끔하게! 🍯)
            return Map.of("result", true, "message", "인증에 성공했습니다.");
        } else {
            return Map.of("result", false, "message", "인증번호가 일치하지 않거나 만료되었습니다.");
        }
    }
}