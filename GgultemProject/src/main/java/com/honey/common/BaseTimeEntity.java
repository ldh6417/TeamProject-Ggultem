package com.honey.common;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
	
	@CreatedDate // 생성 시 자동 저장
	@Column(updatable = false) // 수정 시에는 건드리지 않음
	private LocalDateTime regDate;
	
	@LastModifiedDate // 수정 시 자동 저장
	private LocalDateTime updDate;

}
