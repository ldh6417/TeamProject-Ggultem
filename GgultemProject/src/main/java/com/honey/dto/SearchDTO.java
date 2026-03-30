package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO extends PageRequestDTO {
	
	private String searchType;
	private String keyword;
	
	//관리자용
	private String enabled;
	
	//비즈니스멤버용
	private String businessVerified;
	
	//비즈니스 광고용
	private String sign;
	private String category;
	
	//비즈머니용
	private String state;
}
