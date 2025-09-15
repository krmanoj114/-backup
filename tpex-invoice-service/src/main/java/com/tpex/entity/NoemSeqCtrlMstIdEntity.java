package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class NoemSeqCtrlMstIdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "PKG_MTH")
	private String scmPkgMth;
	
	@Column(name = "MODULE")
	private String scmModule;
	
	@Column(name = "SEQ_CD")
	private String scmSeqCd;

}
