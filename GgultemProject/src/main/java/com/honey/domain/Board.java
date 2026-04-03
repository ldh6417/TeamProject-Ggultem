package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
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
@Table(name = "board")
@SequenceGenerator(name = "BOARD_SEQ_GEN", sequenceName = "BOARD_SEQ1", initialValue = 1, allocationSize = 1)
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Board extends BaseTimeEntity {

	// PK
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BOARD_SEQ_GEN")
	@Column(name = "BOARD_NO")
	private int boardNo;

	// 작성자 (회원)
	@ManyToOne
	@JoinColumn(name = "member_email")
	private Member member;

	private String title;
	private String writer;

	// HTML 내용
	@Column(columnDefinition = "CLOB")
	private String content;

	// 검색용 텍스트
	@Column(name = "content_text", length = 2000)
	private String contentText;

	// 조회수
	private int viewCount;

	// 논리 삭제
	@Builder.Default
	private Integer enabled = 1;

	// 이미지 리스트
	@ElementCollection
	@Builder.Default
	private List<BoardImage> boardImage = new ArrayList<>();

	// 삭제 날짜
	private LocalDateTime dtdDate;

	public void changeTitle(String title) {
		this.title = title;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void changeEnabled(int enabled) {
		this.enabled = enabled;

		if (enabled == 1) {
			this.dtdDate = null;
		} else {
			this.dtdDate = LocalDateTime.now();
		}
	}

	// (HTML + TEXT 같이 처리)
	public void changeContent(String content) {
		this.content = content;
		this.contentText = content.replaceAll("<[^>]*>", "").replaceAll("&nbsp;", " ").trim();
	}

	public void addImage(BoardImage board) {
		board.setOrd(this.boardImage.size());
		boardImage.add(board);
	}

	public void addImageString(String fileName) {
		BoardImage boardImage = BoardImage.builder().fileName(fileName).build();
		addImage(boardImage);
	}

	public void clearList() {
		this.boardImage.clear();
	}
}
