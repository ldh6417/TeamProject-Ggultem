package com.honey.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder // Builder 패턴 객체 생성
@AllArgsConstructor // 매개변수 자동 생성
@NoArgsConstructor // 매개변수 없는 기본 생성

public class BoardDTO {

	private Integer boardNo;

	private Long memberNo; // 작성자 회원번호

	private String title;

	private String writer;

	private String content;

	private Integer viewCount;
	
	private Integer enabled;
	
	

	// ex 2026-03-12T12:33:22 => 2026-03-12 12:33:22
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;

}
