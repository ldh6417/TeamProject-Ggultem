package com.honey.domain;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Entity;
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
@Table(name = "board")
@SequenceGenerator(name = "BOARD_SEQ_GEN", sequenceName = "BOARD_SEQ", initialValue = 1, allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

	private int boardNo;

	@ManyToOne
	@JoinColumn(name = "MEMBER_NO") // 실제 DB 테이블의 FK 컬럼명을 지정
	private Member member;
	
	private String title;
	private String writer;

}
