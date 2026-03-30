package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "business_board")
@SequenceGenerator(name = "BUSINESS_BOARD_SEQ_GEN",
sequenceName = "BUSINESS_BOARD_SEQ",
initialValue = 1,
allocationSize = 1)
@Getter
@ToString(exclude = {"writer", "bItemList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BusinessBoard extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BUSINESS_BOARD_SEQ_GEN")
	private Long no;
	private String title;
	private int price;
	private String category;
	private String content;
	private String writer;
	private String moveUrl;
	private boolean onOff;
	
	@ManyToOne(fetch = FetchType.LAZY) // 지연 로딩 권장
    @JoinColumn(name = "MEMBER_EMAIL") // DB 컬럼명
    private Member member;
	
	private LocalDateTime dtdDate;
	private LocalDateTime endDate;
	private int viewCount;
	private int enabled;
	private boolean sign;
	
	@ElementCollection 
	@Builder.Default 
	private List<BusinessItem> bItemList = new ArrayList<>(); 
	
	public void changeTitle(String title) {
		this.title = title;
	}
	public void changeContent(String content) {
		this.content = content;
	}
	public void changePrice(int price) {
		this.price = price;
	}
	public void changeMoveUrl(String moveUrl) {
		this.moveUrl = moveUrl;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
		LocalDateTime now = LocalDateTime.now();
		
		if(enabled == 0) {
			this.dtdDate = now;
			this.onOff = false;
			this.sign = false;
		}
	}
	
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public void changeSign(boolean sign) {
        this.sign = sign;
    }
	public void setEndDate(LocalDateTime endDate) {
		this.endDate = endDate;
		
		if(LocalDateTime.now().isBefore(endDate)) {
			setOnOff(true);
		} else {
			setOnOff(false);
		}
	}
	
	public void setOnOff(boolean onOff) {
		this.onOff = onOff;
	}
	public void addImage(BusinessItem image) {
		image.setOrd(this.bItemList.size());
		bItemList.add(image);
	}
	public void addImageString(String fileName) {
		BusinessItem bItem = BusinessItem.builder().fileName(fileName).build();
		addImage(bItem);
	}
	public void clearList() {
		this.bItemList.clear();
	}
}
