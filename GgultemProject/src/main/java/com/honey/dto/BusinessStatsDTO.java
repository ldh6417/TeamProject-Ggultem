package com.honey.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BusinessStatsDTO {
	private long totalPowerLinkClicks;
    private long totalPowerLinkCount;
    private long totalPowerShoppingClicks;
    private long totalPowerShoppingCount;
    
    // 월별 데이터를 위한 리스트 (간단하게 월-수치 쌍으로 구성)
    private List<DailyStat> dailyStats;

}
