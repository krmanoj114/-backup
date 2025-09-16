package com.tpex.daily.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class EnginePartMasterIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "IMP_CD")
	private String importerCode;

	@Column(name = "CF_CD")
	private String crFmlyCode;

	@Column(name = "MOD_LOT_CD")
	private String lotModuleCode;

	@Column(name = "PART_NO")
	private String partNo;

	@Column(name = "EXP_CD")
	private String exporterCode;

}
