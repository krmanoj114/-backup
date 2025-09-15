package com.tpex.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_COMPANY")
public class TbMCompany {

	@Id
	@Column(name = "CMP_CD")
	private String companyCode;
	@Column(name = "CMP_NAME")
	private String companyName;
}
