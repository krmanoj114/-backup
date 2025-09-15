package com.tpex.month.model.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_SHIP_CMP")
public class ShippingCompanyMasterEntity {

	@Id
	@Column(name = "CD")
	private String scmCd;
	@Column(name = "NAME")
	private String scmName;
	@Column(name = "EMAIL")
	private String scmEmail;
	@Column(name = "UPD_BY")
	private String scmUpdBy;
	@Column(name = "UPD_DT")
	private Timestamp scmUpdDt;
	
}
