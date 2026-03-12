package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDTO {
	private Long roomId;
	//private ItemBoard itemboard;
	private String buyerId;
	private String sellerId;
	private Integer enabled;
}