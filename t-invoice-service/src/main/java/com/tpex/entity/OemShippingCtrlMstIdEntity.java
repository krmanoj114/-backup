package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OemShippingCtrlMstIdEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "cf_cd")
	private String shCtrlCfCd;

	@Column(name = "imp_dst_cd")
	private String shCtrlImpDstCd;

	@Column(name = "exp_dst_cd")
	private String shCtrlExpDstCd;

	@Column(name = "set_part_cd")
	private String shCtrlSetPartCd;

	@Column(name = "srs_name")
	private String shCtrlSrsName;

	@Column(name = "pm_cd")
	private String shCtrlPmCd;

}
