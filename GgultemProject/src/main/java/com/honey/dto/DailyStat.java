package com.honey.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DailyStat {
    private String day;
    private Long totalCount;   
    private Long powerLinkClick;    // 파워링크 클릭 합계
    private Long powerShoppingClick; // 파워쇼핑 클릭 합계
    
 // 변환 생성자 (Service에서 Interface -> DTO 변환 시 사용)
    public DailyStat(DailyStatInterface mi) {
        this.day = mi.getDay();
        this.totalCount = mi.getTotalCount();
        this.powerLinkClick = mi.getPowerLinkClick() != null ? mi.getPowerLinkClick() : 0L;
        this.powerShoppingClick = mi.getPowerShoppingClick() != null ? mi.getPowerShoppingClick() : 0L;
    }
    
    public DailyStat(String day, Long totalCount, Long powerLinkClick, Long powerShoppingClick) {
    	this.day = day;
    	this.totalCount = totalCount;
    	this.powerLinkClick = powerLinkClick;
    	this.powerShoppingClick = powerShoppingClick;
    }
}
