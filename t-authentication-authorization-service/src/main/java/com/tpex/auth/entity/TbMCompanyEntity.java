package com.tpex.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_m_company")
public class TbMCompanyEntity {

	@Id
	@Column(name = "CMP_CD", length = 10, nullable = false)
	private String companyCode;
	@Column(name = "CMP_NAME", length = 20, nullable = false)
	private String companyName;
}
