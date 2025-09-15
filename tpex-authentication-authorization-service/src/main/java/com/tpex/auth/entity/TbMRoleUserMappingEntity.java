package com.tpex.auth.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "tb_m_role_user_mapping")
public class TbMRoleUserMappingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "RUM_ID")
	private Long idMapping;
	@Column(name = "USER_ID", length = 10)
	private String userId;
	@Column(name = "ROLE_ID")
	private Long roleId;
}
