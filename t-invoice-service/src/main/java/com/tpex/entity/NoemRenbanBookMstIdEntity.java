package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Embeddable
@Data
public class NoemRenbanBookMstIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name="CONT_VAN_MTH")
	private String contVanningMnth;

	@Column(name="CONT_DST_CD")
	private String contDestCd;

	@Column(name = "ETD1")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd1;
	public LocalDate getEtd1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd1);
	}

	@Column(name="SHP_NM_1")
	private String shpName1;

	@Column(name="GROUP_ID")
	private String groupId;

}
