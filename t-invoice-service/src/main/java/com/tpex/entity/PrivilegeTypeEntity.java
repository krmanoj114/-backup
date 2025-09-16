package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_M_PRIVILEGE_TYPE")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PrivilegeTypeEntity {

	@Id
	@Column(name = "PRIV_CD")
	private String privilegeCode;
	
	@Column(name = "PRIV_NM")
	private String privilegeName;
	
	@Column(name = "PRIV_MAP_CD")
	private String privMapCd;
	
	@Column(name = "SEP_INV_CD")
	private String setInvCd;
	
	@Column(name = "PRIV_TYPE")
	private String privType;
	
	@Column(name = "PRIV_GROUP")
	private String privGroup;
	
	@Column(name = "TMSD_CD")
	private String tmsdCd;
	
	@Column(name = "SORT_SEQ")
	private Integer sortSeq;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
