package com.tpex.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsigneeAndNotifyPartyDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<CommonMultiSelectDropdownDto> consigneeList;
	private List<CommonMultiSelectDropdownDto> notifyPartyList;
}
