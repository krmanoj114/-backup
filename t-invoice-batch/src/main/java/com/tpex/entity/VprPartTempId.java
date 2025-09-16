package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class VprPartTempId implements Serializable {

	private static final long serialVersionUID = -6593680879135661059L;
	
	private LocalDateTime procDt;
	private String intrId;
	private String mainFlg;
	private String contDstCd;
	private String contSno;
	private String modDstCd;
	private String lotModNo;
	private String caseNo;
	private String boxSeqNo;
	private String partNo;
}
