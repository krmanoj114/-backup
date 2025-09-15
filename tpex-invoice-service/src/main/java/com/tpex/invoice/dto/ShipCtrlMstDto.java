package com.tpex.invoice.dto;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipCtrlMstDto implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private String buyer;
	private String impCode;
	private String expCode;
	private String cfcCode;
	private String series;
	private String setPartCode;
	private String portOfDischarge;
	private String productGroup;
	private String folderName;
	private String goodDesc1;
	private String goodDesc2;
	private String goodDesc3;
	private List<CommonMultiSelectDropdownDto> consigneeList;
	private String consignee;
	private List<CommonMultiSelectDropdownDto> notifyPartyList;
	private String notifyParty;
	private String tradeTerm;
	private String certificationOfOriginReport;
	private String soldToMessrs;
	private String plsFlag;
	@JsonFormat(pattern="dd/MM/yyyy HH:mm")
	private Timestamp updateDateTime;
	public LocalDateTime getUpdateDateTime() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDateTime);
	}
}
