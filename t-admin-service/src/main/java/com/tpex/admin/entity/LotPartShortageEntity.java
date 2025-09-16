package com.tpex.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_R_LOT_PART_SHORT_D")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class LotPartShortageEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private LotPartShortageIdEntity lotPartShortageIdEntity;

	@Column(name = "CONT_SNO")
	private String contSno;

	@Column(name = "PART_NM")
	private String partNm;

	@Column(name = "PLN_QTY")
	private Double plnQty;

	@Column(name = "ACT_QTY")
	private Double actQty;

	@Column(name = "CREATE_DT")
	private Date createDate;

	@Column(name = "CREATE_BY")
	private String createBy;

	@Column(name = "UPD_DT")
	private Date updDate;

	@Column(name = "UPD_BY")
	private String updBy;

	@Column(name = "INV_NO")
	private String invNo;

	@Column(name = "SRS_NM")
	private String srsNm;

	@Column(name = "REXP_CD")
	private String rexpCd;

	@Column(name = "ORD_SRC")
	private String ordSrc;

	@Column(name = "CMP_CD")
	private String cmpCd;

}
