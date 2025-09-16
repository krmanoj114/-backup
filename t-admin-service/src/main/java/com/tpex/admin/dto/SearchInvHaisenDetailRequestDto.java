package com.tpex.admin.dto;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchInvHaisenDetailRequestDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	private Date etdFrom;

	@JsonFormat(pattern = "dd/MM/yyyy")
	public LocalDate getEtdFrom() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etdFrom);
	}

	private Date etdTo;

	@JsonFormat(pattern = "dd/MM/yyyy")
	public LocalDate getEtdTo() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etdTo);
	}

	private String buyer;

	private OrderType orderType;

	@JsonIgnore
	private boolean hasParameter;
}
