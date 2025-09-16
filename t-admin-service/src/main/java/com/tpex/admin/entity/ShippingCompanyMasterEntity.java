package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "tb_m_ship_cmp")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ShippingCompanyMasterEntity implements Serializable{
	@Id
	@Column(name = "CD")
	private String cd;
	
	
	@Column(name = "SHIPNAME")
	private String shipName;
	
	@Column(name = "EMAIL")
	private String email;
	
	
	
	@Column(name = "UPD_BY")
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	private String updatedDate;
	
	@Column(name = "CMP_CD")
	private String cmpCd;

}
