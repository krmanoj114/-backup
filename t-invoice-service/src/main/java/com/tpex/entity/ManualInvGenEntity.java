package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Table(name = "tb_r_dly_vpr_container")
@Entity

public class ManualInvGenEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "CONT_DST_CD")
	private String contDstCd;
	
	@Column(name = "CONT_SNO")
	private String contSno;

	@Column(name = "VAN_PLNT_CD")
	private String vanPlntCd;

}
