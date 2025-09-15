/**
 * 
 */
package com.tpex.invoice.service;

import java.io.IOException;
import java.util.List;

import com.tpex.dto.ConsigneeAndNotifyPartyDto;
import com.tpex.dto.OemShippingCtrlMstDeleteRequestDto;
import com.tpex.dto.OemShippingCtrlMstSaveRequestDto;
import com.tpex.invoice.dto.OemShippingCtrlMstDto;

public interface InvShippingCtrlMstService {
	
	/**
	 * @return OemShippingCtrlMstDto
	 * @throws Exception 
	 */
	OemShippingCtrlMstDto fetchInvShippingCtrlMst() throws IOException;

	/**
	 * @param buyer
	 * @return
	 */
	ConsigneeAndNotifyPartyDto fetchConsigneeAndNotifyPartyByBuyer(String buyer);

	boolean saveShippingControlMaster(List<OemShippingCtrlMstSaveRequestDto> oemShippingCtrlMstSaveRequestDtoList);

	void deleteShippingControlMaster(List<OemShippingCtrlMstDeleteRequestDto> oemShippingCtrlMstDeleteRequestDtoList);

}
