package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ReturnablePackingMasterIdEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name = "IMP_CD")
	private String impCd;

	@Column(name = "MOD_TYP")
	private String modType;

	@Column(name = "VAN_FROM")
	@JsonFormat(pattern = ConstantUtils.DEFAULT_DATE_FORMATE)
	private Date vanFrom;

	public LocalDate getVanFrom() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(vanFrom);
	}

	@Column(name = "PLNT_CD")
	private String plantCd;

}
