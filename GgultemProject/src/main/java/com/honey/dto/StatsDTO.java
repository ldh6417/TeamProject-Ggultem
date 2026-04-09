package com.honey.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDTO {
	private long memberCount;     // 회원 등록 수
    private long itemCount;       // 중고거래 등록 수
    private long adCount;         // 비즈니스 광고 등록 수
    private long totalCharge;     // 비즈머니 충전 총합
    private long totalSales;      // 비즈머니 지출 총합 (매출)
}
