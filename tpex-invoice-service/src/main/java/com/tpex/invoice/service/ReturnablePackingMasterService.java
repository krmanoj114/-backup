package com.tpex.invoice.service;

import java.util.List;

import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;

public interface ReturnablePackingMasterService {
	
	/**
	 * @return ReturnablePackingMasterDTO
	 * @throws Exception 
	 */
	List<OemFnlDstMstEntity> destinationCodeList();
	List<PlantMstEntity> packingPlantList(String cmpCd);
	List<CarFamilyMasterEntity> carFamilyList();
	ReturnablePackingMasterImporterDTO fetchReturnablePackingMasterDetails(String packingPlant, String importerCode,
			String moduleType, String cmpCd);

}
