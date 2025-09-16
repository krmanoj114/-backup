package com.tpex.admin.entity;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_M_PROD_GRP")
@Entity
public class InsProdGrpMstEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "PROD_GRP_CD")
	private String ipgProdGrpCd;

	@Column(name = "PROD_GRP_DESC")
	private String ipgProdGrpDesc;

	@Column(name = "UPD_BY")
	private String ipgUpdBy;

	@Column(name = "UPD_DT")
	private Date ipgUpdDate;

	@Column(name = "CMP_CD")
	private Date companyCode;
}
