package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessBoardDTO {
	
	private Long no;
	private String title;
	private Integer price;
	private String category;
	private String content;
	private String email;
	private String writer;
	private Integer viewCount;
	private Integer enabled;
	private Boolean sign;
	private String moveUrl;
	private Boolean onOff;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime dtdDate;
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime endDate;
	
	@Builder.Default
	private List<MultipartFile> files = new ArrayList<>();
	
	@Builder.Default
	private List<String> uploadFileNames = new ArrayList<>();
	
	
}
