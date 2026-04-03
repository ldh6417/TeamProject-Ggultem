package com.honey.service;

import java.util.Random;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.honey.util.RedisUtil;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class MailServiceImpl implements MailService {

    private final JavaMailSender javaMailSender; 
    private final RedisUtil redisUtil;

    @Override
    public String sendMail(String email) {
    	String authCode = String.valueOf(new Random().nextInt(888888) + 111111);
    	
    	redisUtil.setDataExpire(email, authCode, 5);

        // 2. 메일 메시지 작성
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[꿀템] 인증번호 안내 🍯");
            
            // HTML 형식으로 메일 보내기
            String content = "<div style='margin:20px; border:1px solid #ffca28; padding:20px; border-radius:10px;'>"
                    + "<h2>안녕하세요, 꿀템입니다!</h2>"
                    + "<p>아래 인증번호를 입력해주세요.</p>"
                    + "<div style='background:#fff9c4; padding:10px; font-size:24px; font-weight:bold; text-align:center;'>"
                    + authCode
                    + "</div>"
                    + "</div>";
            
            helper.setText(content, true);
            javaMailSender.send(mimeMessage); // 3. 발송!
            
            log.info("메일 발송 완료! 대상: {}, 인증번호: {}", email, authCode);
            return authCode; // 발송 성공 시 생성된 코드를 리턴 (나중에 검증용)

        } catch (MessagingException e) {
            log.error("메일 발송 실패 ❌", e);
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다.");
        }
    }
}