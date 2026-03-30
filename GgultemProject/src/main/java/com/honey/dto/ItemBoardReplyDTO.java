package com.honey.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemBoardReplyDTO {

	private Long replyNo;
	private Long itemId;
	private String itemTitle;
	private String content;
	
	private String email;
	private String nickname;
	private Long parentReplyNo;
	
	@Builder.Default
	private List<ItemBoardReplyDTO> childList = new ArrayList<>();
	
	private Integer enabled;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime regDate;
	
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updDate;
}
