package com.tpex.month.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tpex.month.model.dto.CustomBrokerMasterDropdown;
import com.tpex.month.model.dto.DropDownVesselBookingMasterResponse;
import com.tpex.month.model.repository.CustomBrokerMasterRepository;
import com.tpex.month.model.repository.FinalDestinationMasterRepository;
import com.tpex.month.model.repository.ShippingCompanyMasterRepository;
import com.tpex.month.util.ConstantUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DropdownService {

	private final FinalDestinationMasterRepository finalDestinationMasterRepository;
	private final ShippingCompanyMasterRepository shippingCompanyMasterRepository;
	private final CustomBrokerMasterRepository customBrokerMasterRepository;
	
	public DropDownVesselBookingMasterResponse getDropDownForVesselBookingMaster() {
		return new DropDownVesselBookingMasterResponse(finalDestinationMasterRepository.findAllProjectedCodeAndNameByOrderByFdDstCd()
				, shippingCompanyMasterRepository.findAllProjectedCodeByOrderByScmCd());
	}
	
	public List<CustomBrokerMasterDropdown> getCustomBrokerMasterDropDown() {
		return customBrokerMasterRepository.findAllProjectedCodeAndNameByOrderByCbmCbCd().stream().map(b -> {
			CustomBrokerMasterDropdown dd = new CustomBrokerMasterDropdown();
			dd.setCustomBrokerCode(b.getCbmCbCd());
			dd.setCustomBrokerDisplay(b.getCbmCbCd() + ConstantUtil.WHITESPACE + b.getCbmCbNm());
			return dd;
		}).collect(Collectors.toList());
	}
}
