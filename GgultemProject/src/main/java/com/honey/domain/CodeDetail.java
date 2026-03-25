package com.honey.domain;

import com.honey.common.BaseTimeEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "CODE_DETAIL")
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@IdClass(CodeDetailId.class)
public class CodeDetail extends BaseTimeEntity {
	
	@Id
	private String groupCode;
	@Id
	private String codeValue;
	
	private String codeName;
	private Integer sortSeq;
	private String useYn;
	private Integer enabled;

	public void changeEnabled(int enabled) {
		this.enabled = enabled;
	}

	public void changeCodeName(String codeName) {
		this.codeName = codeName;
	}
	
	public void changeUseYn(String useYn) {
		this.useYn = useYn;
	}

}
