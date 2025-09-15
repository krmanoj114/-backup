package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class NoemRenbanSetupMstIdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CONT_DST_CD")
	private String contDstCd;
	
	@Column(name = "EFF_FROM_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date effFromDt;
	
	public LocalDate getEtd1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(effFromDt);
	}
	
	@Column(name = "GROUP_ID")
	private String groupId;
	
	@Column(name = "CONT_GRP_CD")
	private String contGrpCd;
	
	

}
