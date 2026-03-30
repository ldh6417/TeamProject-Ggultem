package com.honey.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "ItemBoard_reply")
@SequenceGenerator(name = "ITEMBOARD_REPLY_GEN", sequenceName = "ITEMBOARD_REPLY_SEQ", allocationSize = 1)
@Getter
@ToString(exclude = {"itemBoard","member","parent","childList"})
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ItemBoardReply {

	@Id
	@Column(name = "REPLY_NO")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ITEMBOARD_REPLY_GEN")
	private Long replyNo;
	
	@ManyToOne
	@JoinColumn(name = "ITEMBOARD_ID", nullable = false)
	private ItemBoard itemBoard;
	
	
	@ManyToOne
	@JoinColumn(name = "MEMBER_EMAIL", nullable = false)
	private Member member;
	
	@ManyToOne
	@JoinColumn(name = "PARENT_REPLY_ID", nullable = true)
	private ItemBoardReply parent;
	
	@OneToMany(mappedBy = "parent")
	@Builder.Default
	private List<ItemBoardReply> childList = new ArrayList<>();
	
	@JoinColumn(nullable = false)
	private String content;
	
	private Integer enabled;
	
	@CreatedDate
    @Column(name = "reg_date", updatable = false)
	private LocalDateTime regDate;
	
	
	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}
	public void changeContent(String content) {
		this.content = content;
	}

	
	
}
