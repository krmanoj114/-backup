package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class EngVinMasterIdEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "IMP_CD")
	private String impCode;
	@Column(name = "CF_CD")
	private String cfCd;
	@Column(name = "MOD_LOT_CD")
	private String modLotCode;
	@Column(name = "PART_NO")
	private String partNo;
	@Column(name = "EXP_CD")
	private String expCode;
}
