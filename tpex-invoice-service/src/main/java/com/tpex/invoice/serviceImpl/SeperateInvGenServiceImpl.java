package com.tpex.invoice.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.entity.SeperateInvDetailsEntity;
import com.tpex.invoice.dto.SeperateInvoiceDto;
import com.tpex.invoice.dto.SeprateInvGenResponseDto;
import com.tpex.invoice.dto.SeprateInvGenResponseWrapper;
import com.tpex.invoice.service.SeperateInvGenService;
import com.tpex.repository.SeperateInvDetailsRepository;

@Service

public class SeperateInvGenServiceImpl implements SeperateInvGenService {

	@Autowired
	SeperateInvDetailsRepository seperateInvDetailsRepository;

	@Override
	public SeprateInvGenResponseWrapper searchByInvNo(String invoiceNo, String carFamily, String partNo) {

		if (carFamily.isEmpty()) {
			carFamily = null;
		}
		if (partNo.isEmpty()) {
			partNo = null;
		}
		List<Object[]> objRespose = seperateInvDetailsRepository.getinvpartdata(invoiceNo, carFamily, partNo);

		List<SeperateInvDetailsEntity> list = seperateInvDetailsRepository.findByInvNoEquals(invoiceNo);
		List<SeperateInvoiceDto> setInvList = new ArrayList<>();
		for (SeperateInvDetailsEntity listObj : list) {
			SeperateInvoiceDto seperateInvoiceDto = new SeperateInvoiceDto();
			seperateInvoiceDto.setDispInvNo(listObj.getDispInvNo());
			setInvList.add(seperateInvoiceDto);
		}

		List<SeprateInvGenResponseDto> response = new ArrayList<>();
		if (!objRespose.isEmpty()) {

			for (Object[] obj : objRespose) {
				SeprateInvGenResponseDto dto = new SeprateInvGenResponseDto();

				if (obj[0] != null)
					dto.setImpCd(obj[0].toString());
				if (obj[1] != null)
					dto.setPartNo(obj[1].toString());
				if (obj[2] != null)
					dto.setCfCd(obj[2].toString());
				if (obj[3] != null)
					dto.setSeries(obj[3].toString());
				if (obj[4] != null && obj[5] != null)
					dto.setModifiedPrivilage(obj[4].toString() + "-" + obj[5].toString());
				if (obj[5] != null)
					dto.setOriginalPrivilage(obj[5].toString());
				response.add(dto);
			}
		}

		SeprateInvGenResponseWrapper seprateInvGenResponseWrapper = new SeprateInvGenResponseWrapper();
		seprateInvGenResponseWrapper.setSepInvDto(setInvList);
		seprateInvGenResponseWrapper.setSepInvGenRespDto(response);
		return seprateInvGenResponseWrapper;
	}

}
