package com.honey.service;

public interface MailService {
    // 인증 번호를 생성하고 이메일을 발송
    String sendMail(String email);
}