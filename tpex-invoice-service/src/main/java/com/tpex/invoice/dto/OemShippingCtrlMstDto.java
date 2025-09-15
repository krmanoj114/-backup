package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OemShippingCtrlMstDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CommonDropdownDto> buyerList;
	
	private List<CommonDropdownDto> impCodeList;
	
	private List<CommonDropdownDto> expCodeList;
	
	private List<CommonMultiSelectDropdownDto> cfcCodeList;
	
	private List<CommonMultiSelectDropdownDto> seriesList;
	
	private List<CommonDropdownDto> setPartCodeList;
	
	private List<CommonMultiSelectDropdownDto> portOfDischargeList;
	
	private List<CommonMultiSelectDropdownDto> productGroupList;
	
	private List<CommonDropdownDto> tradeTermList;

	private List<CommonDropdownDto> certificationOfOriginReportList;
	
	private List<CommonMultiSelectDropdownDto> soldToMessrsList;
	
	private List<ShipCtrlMstDto> masterList;
	
}
