package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class VprModuleTempId implements Serializable {

	private static final long serialVersionUID = -8097279087086305933L;

	private LocalDateTime procDt;
	private String intrId;
	private String mainFlg;
	private String contDstCd;
	private String contSno;
	private String modDstCd;
	private String lotModNo;
	private String caseNo;

}
