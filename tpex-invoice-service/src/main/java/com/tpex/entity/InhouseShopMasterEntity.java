package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "tb_m_inh_shop")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class InhouseShopMasterEntity {

	@Id
	@Column(name = "INS_SHOP_CD")
	private String insShopCd;
	@Column(name = "DESCRIPTION")
	private String description;
	@Column(name = "UPD_BY")
	private String updBy;
	@Column(name = "UPD_DT")
	private String updDt;
	@Column(name = "INHOUSE_PLANT")
	private String inhousePlant;
	@Column(name = "CMP_CD")
	private String cmpCd;
	
}
