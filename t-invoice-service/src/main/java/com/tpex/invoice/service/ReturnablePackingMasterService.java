package com.tpex.invoice.service;

import java.text.ParseException;
import java.util.List;

import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.PlantMstEntity;
import com.tpex.invoice.dto.ReturnablePackingMasterDetailsDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterImporterDTO;
import com.tpex.invoice.dto.ReturnablePackingMasterSaveRequestDTO;

public interface ReturnablePackingMasterService {

	/**
	 * @return ReturnablePackingMasterDTO
	 * @throws Exception
	 */
	List<OemFnlDstMstEntity> destinationCodeList();

	List<PlantMstEntity> packingPlantList(String cmpCd);

	List<CarFamilyMasterEntity> carFamilyList();

	ReturnablePackingMasterImporterDTO fetchReturnablePackingMasterDetails(String packingPlant, String moduleType,
			String cmpCd, String returnableType, String vanDateFrom, String vanDateTo, List<String> importerCode)
			throws ParseException;

	boolean saveReturnablePackingMaster(ReturnablePackingMasterSaveRequestDTO returnablePackingMasterSaveRequestDTO,
			String userId);

	void deleteReturnablePackingMaster(List<ReturnablePackingMasterDetailsDTO> returnablePackingMasterDetailsDTOList);

}
