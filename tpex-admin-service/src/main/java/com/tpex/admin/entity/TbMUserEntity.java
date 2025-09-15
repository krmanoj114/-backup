package com.tpex.admin.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_m_user")
public class TbMUserEntity {

	@Id
	@Column(name = "USER_ID", nullable = false, length = 10)
	private String userId;
	@Column(name = "NAME", length = 25)
	private String name;
	@Column(name = "SURNAME", length = 25)
	private String surname;
	@Column(name = "EMAIL", length = 50)
	private String email;
	@Column(name = "CMP_CD", length = 10, nullable = false)
	private String companyCode;
	@Column(name = "AZURE_UNIQUE_NAME", length = 100, unique = true)
	private String azureUniqueName;
}
