package com.tpex.invoice.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.ManualInvoiceGenResponseDto;
import com.tpex.invoice.service.ManualInvGenService;
import com.tpex.repository.ManualInvGenRepository;
import com.tpex.util.ConstantUtils;

@Service
public class ManualInvGenServiceImpl implements ManualInvGenService {

	

	@Autowired
	ManualInvGenRepository manualInvGenRepository;

	

	public List<ManualInvoiceGenResponseDto> searchManualInvDetails(Map<String, String> queryParam) {
		// ManualInvoiceGenResponseDto
		String etdDate = null;
		String destCode = null;
		String invoiceType = null;
		String cmpCd = null;
		if (null != queryParam && StringUtils.isNotEmpty(queryParam.get(ConstantUtils.ETD_INVOICE_DATE))) {
			etdDate = queryParam.get(ConstantUtils.ETD_INVOICE_DATE);
		}
		if (null != queryParam && StringUtils.isNotEmpty(queryParam.get(ConstantUtils.DEST_CODE))) {
			destCode = queryParam.get(ConstantUtils.DEST_CODE);
		}
		if (null != queryParam && StringUtils.isNotEmpty(queryParam.get(ConstantUtils.INVOICE_TYPE))) {
			invoiceType = queryParam.get(ConstantUtils.INVOICE_TYPE);
		}
		if (null != queryParam && StringUtils.isNotEmpty(queryParam.get(ConstantUtils.COMPANY_CODE))) {
			cmpCd = queryParam.get(ConstantUtils.COMPANY_CODE);
		}

		List<Object[]> manualInvoiceList = manualInvGenRepository.getManualInvoiceData(etdDate, destCode, invoiceType,
				cmpCd);
		List<ManualInvoiceGenResponseDto> listInvoiceData = new ArrayList<>();

		if (manualInvoiceList != null && !manualInvoiceList.isEmpty()) {
			for (Object[] obj : manualInvoiceList) {
				ManualInvoiceGenResponseDto manualInvoiceGenResponseDto = new ManualInvoiceGenResponseDto();
				setValuesInDtoObject(obj, manualInvoiceGenResponseDto);
				listInvoiceData.add(manualInvoiceGenResponseDto);

			}

		}
		return listInvoiceData;
	}

	private void setValuesInDtoObject(Object[] obj, ManualInvoiceGenResponseDto manualInvoiceGenResponseDto) {
		if (obj[2] != null)
			manualInvoiceGenResponseDto.setRanbanNumber(obj[2].toString());
		if (obj[4] != null)
			manualInvoiceGenResponseDto.setShippingResult(obj[4].toString());
		if (obj[7] != null)
			manualInvoiceGenResponseDto.setInvoiceStatus(obj[7].toString());
		if (obj[8] != null)
			manualInvoiceGenResponseDto.setVanPlant(obj[8].toString());
		if (obj[9] != null) {
			manualInvoiceGenResponseDto.setType(obj[9].toString());
		}
	}

}
