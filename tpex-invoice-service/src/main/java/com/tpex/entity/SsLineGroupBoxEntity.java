package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity

@Table(name = "TB_M_SSL_PCK")

public class SsLineGroupBoxEntity {

	@Id
	@Column(name = "CF_CD")
	private String cfCode;
	
	@Column(name = "GRP_CD")
	private String groupCode;
	
//	@Column(name = "companyCode")
//	private String 
	
	

}
