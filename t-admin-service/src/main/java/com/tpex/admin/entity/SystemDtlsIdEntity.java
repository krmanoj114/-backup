package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemDtlsIdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CMP_CD")
	private String companyCd;
	
	@Column(name = "Name")
	private String systemName;
}
