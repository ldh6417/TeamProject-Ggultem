package com.honey.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.honey.domain.Member;

public class BoardDTO {
	
	private int boardNo;
	private Member member;
	private String title;
	private String writer;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	
	
}
