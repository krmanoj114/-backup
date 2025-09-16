package com.tpex.invoice.serviceimpl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.CommonDropdownDto;
import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.ConsigneeAndNotifyPartyDto;
import com.tpex.dto.OemShippingCtrlMstDeleteRequestDto;
import com.tpex.dto.OemShippingCtrlMstSaveRequestDto;
import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.InsProdGrpMstEntity;
import com.tpex.entity.OemCnshMst;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.entity.OemShippingCtrlMstEntity;
import com.tpex.entity.OemShippingCtrlMstIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.OemShippingCtrlMstDto;
import com.tpex.invoice.dto.ShipCtrlMstDto;
import com.tpex.invoice.service.InvShippingCtrlMstService;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.InsProdGrpMstRepository;
import com.tpex.repository.OemCnshMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.OemShippingCtrlMstRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.TpexConfigurationUtil;

@Service
public class InvShippingCtrlMstServiceImpl implements InvShippingCtrlMstService {

	@Autowired
	private OemShippingCtrlMstRepository oemShippingCtrlMstRepository;
	
	@Autowired
	private OemCnshMstRepository oemCnshMstRepository;
	
	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@Autowired
	private CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;
	
	@Autowired
	private OemPortMstRepository oemPortMstRepository;
	
	@Autowired
	private InsProdGrpMstRepository insProdGrpMstRepository;
	
	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	
	@Override
	public OemShippingCtrlMstDto fetchInvShippingCtrlMst() throws IOException {
		
		//Main list to be returned
		OemShippingCtrlMstDto oemShippingCtrlMstDto = new OemShippingCtrlMstDto();

		//Fetch data from Shipping Control Master
		List<OemShippingCtrlMstEntity> oemShippingCtrlMstEntityList = oemShippingCtrlMstRepository.findAllByOrderByIdShCtrlImpDstCdAscIdShCtrlCfCdAsc();
		
		//List of all buyers from Address master
		List<CommonDropdownDto> shipCtrlBuyerDtoList = new ArrayList<>();
		List<OemCnshMst> oemCnshMstList = oemCnshMstRepository.findAllByOrderByIdCmpCdAsc();
		oemCnshMstList.stream().forEach(c -> shipCtrlBuyerDtoList.add(new CommonDropdownDto(c.getId().getCmpCd(), c.getId().getCmpCd())));
		List<CommonDropdownDto> shipCtrlBuyerList = shipCtrlBuyerDtoList.stream().distinct()
				.collect(Collectors.toList());
			
		oemShippingCtrlMstDto.setBuyerList(shipCtrlBuyerList);
	
		//List of all imp & exp codes from Final Destination master
		List<CommonDropdownDto> shipCtrlImpExpCodeDtoList = new ArrayList<>();
		List<OemFnlDstMstEntity> oemFnlDstMstEntityList = oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc();
		oemFnlDstMstEntityList.stream().forEach(c -> shipCtrlImpExpCodeDtoList.add(new CommonDropdownDto(c.getFdDstCd(), c.getFdDstCd())));
		List<CommonDropdownDto> shipCtrlImpExpCodeList = shipCtrlImpExpCodeDtoList.stream().distinct()
				.collect(Collectors.toList());
		
		oemShippingCtrlMstDto.setImpCodeList(shipCtrlImpExpCodeList);
		oemShippingCtrlMstDto.setExpCodeList(shipCtrlImpExpCodeList);
	
		//List of car family code from Car Family Master
		List<CommonMultiSelectDropdownDto> shipCtrlCfcCodeDtoList = new ArrayList<>();
		List<CarFamilyMasterEntity> carFamilyMasterEntityList = carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc();
		carFamilyMasterEntityList.stream().forEach(c -> shipCtrlCfcCodeDtoList.add(new CommonMultiSelectDropdownDto(c.getCarFmlyCode(), c.getCarFmlyCode().length() > 4 ? c.getCarFmlyCode().substring(0, 4) : c.getCarFmlyCode())));
		
		oemShippingCtrlMstDto.setCfcCodeList(shipCtrlCfcCodeDtoList.stream().distinct()
				.collect(Collectors.toList()));
			
		//List of series from Car Family Master
		List<CommonMultiSelectDropdownDto> shipCtrlSeriesDtoList = new ArrayList<>();

		List<CarFamilyDestinationMasterEntity> carFamilyMasterEntities = carFamilyDestinationMasterRepository.findAllByOrderBySrsNameAsc();
		carFamilyMasterEntities.stream().forEach(c -> shipCtrlSeriesDtoList.add(new CommonMultiSelectDropdownDto(c.getSrsName(), c.getSrsName())));
		
		oemShippingCtrlMstDto.setSeriesList(shipCtrlSeriesDtoList.stream().distinct()
				.collect(Collectors.toList()));
		
		//Set part code list with fixed value
		List<CommonDropdownDto> shipCtrlSetPartCodeDtoList = new ArrayList<>();
		shipCtrlSetPartCodeDtoList.add(new CommonDropdownDto("L", "L Lot"));
		shipCtrlSetPartCodeDtoList.add(new CommonDropdownDto("P", "P PxP"));
		
		oemShippingCtrlMstDto.setSetPartCodeList(shipCtrlSetPartCodeDtoList);
			
		//List of series from Car Family Master
		List<CommonMultiSelectDropdownDto> shipCtrlPortOfDischargeDtoList = new ArrayList<>();
		List<OemPortMstEntity> oemPortMstEntityList = oemPortMstRepository.findAllByOrderByCdAsc();
		oemPortMstEntityList.stream().forEach(c -> shipCtrlPortOfDischargeDtoList.add(new CommonMultiSelectDropdownDto(c.getCd(), c.getCd() + "-" + c.getName())));
		
		oemShippingCtrlMstDto.setPortOfDischargeList(shipCtrlPortOfDischargeDtoList.stream().distinct()
				.collect(Collectors.toList()));
			
		//List of product group from Product Group Master
		List<CommonMultiSelectDropdownDto> shipCtrlProductGroupDtoList = new ArrayList<>();
		List<InsProdGrpMstEntity> insProdGrpMstEntityList = insProdGrpMstRepository.findAllByOrderByIpgProdGrpCd();
		insProdGrpMstEntityList.stream().forEach(c -> shipCtrlProductGroupDtoList.add(new CommonMultiSelectDropdownDto(c.getIpgProdGrpCd(), c.getIpgProdGrpCd() + "-" + c.getIpgProdGrpDesc())));
		
		oemShippingCtrlMstDto.setProductGroupList(shipCtrlProductGroupDtoList.stream().distinct()
				.collect(Collectors.toList()));

	
		//List of trade term from Json configuration
		ObjectMapper objectMapper = new ObjectMapper();
		String filePath = tpexConfigurationUtil.getFilePath("shipCtrlScreenTradeTermList");
		File file = ResourceUtils.getFile(filePath);
		if(StringUtils.isBlank(filePath)) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filename", "shipCtrlScreenTradeTermList");
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3017, errorMessageParams); 
		}
		
		List<CommonDropdownDto> shipCtrlTradeTermDtoList = Arrays.asList(objectMapper.readValue(file, CommonDropdownDto[].class));	
			
		oemShippingCtrlMstDto.setTradeTermList(shipCtrlTradeTermDtoList);

		//Certification of Origin Report list with fixed values
		List<CommonDropdownDto> certificationOfOriginReportList = new ArrayList<>();
		certificationOfOriginReportList.add(new CommonDropdownDto("Y", "Y"));
		certificationOfOriginReportList.add(new CommonDropdownDto("N", "N"));
		
		oemShippingCtrlMstDto.setCertificationOfOriginReportList(certificationOfOriginReportList);
			
		//List of sold to messrs from address master
		List<CommonMultiSelectDropdownDto> shipCtrlSoldToMessrsDtoList = new ArrayList<>();
		oemCnshMstList.stream().forEach(c -> shipCtrlSoldToMessrsDtoList.add(new CommonMultiSelectDropdownDto(c.getId().getCmpCd(), c.getId().getCmpCd() + "-" + c.getCmpName())));
		
		oemShippingCtrlMstDto.setSoldToMessrsList(shipCtrlSoldToMessrsDtoList.stream().distinct()
				.collect(Collectors.toList()));
		
		List<OemCnshMst> list = oemCnshMstRepository.findAllByOrderByIdCmpBranchAsc();

		List<ShipCtrlMstDto> shipCtrlMstDtos = new ArrayList<>();
		if (!oemShippingCtrlMstEntityList.isEmpty()) {
			oemShippingCtrlMstEntityList.stream().forEach(i -> {
				ShipCtrlMstDto shipCtrlMstDto = new ShipCtrlMstDto();
				shipCtrlMstDto.setBuyer(i.getShCtrBuyer());
				shipCtrlMstDto.setImpCode(i.getId().getShCtrlImpDstCd());
				shipCtrlMstDto.setExpCode(i.getId().getShCtrlExpDstCd());
				shipCtrlMstDto.setCfcCode(i.getId().getShCtrlCfCd());
				shipCtrlMstDto.setSeries(i.getId().getShCtrlSrsName());
				shipCtrlMstDto.setSetPartCode(i.getId().getShCtrlSetPartCd());
				shipCtrlMstDto.setPortOfDischarge(i.getId().getShCtrlPmCd());
				shipCtrlMstDto.setProductGroup(i.getShCtrlProdGrpCd());
				shipCtrlMstDto.setFolderName(i.getShCtrlFolderNm());
				shipCtrlMstDto.setGoodDesc1(i.getShCtrlGoodsDesc1());
				shipCtrlMstDto.setGoodDesc2(i.getShCtrlGoodsDesc2());
				shipCtrlMstDto.setGoodDesc3(i.getShCtrlGoodsDesc3());
				shipCtrlMstDto.setConsignee(i.getShCtrlCnsg());
				shipCtrlMstDto.setNotifyParty(i.getShCtrlNtfPartyDtls());
				
				List<CommonMultiSelectDropdownDto> shipCtrlConsigneeAndNotifyPartyDtoList = new ArrayList<>();
				list.stream().filter(c -> i.getShCtrBuyer().equals(c.getId().getCmpCd())).forEach(c -> shipCtrlConsigneeAndNotifyPartyDtoList.add(new CommonMultiSelectDropdownDto(c.getId().getCmpBranch(), c.getId().getCmpBranch() + "-" + c.getCmpName())));
				
				shipCtrlMstDto.setConsigneeList(shipCtrlConsigneeAndNotifyPartyDtoList);
				shipCtrlMstDto.setNotifyPartyList(shipCtrlConsigneeAndNotifyPartyDtoList);
				
				
				shipCtrlMstDto.setTradeTerm(i.getShCtrlTradeTermCd());
				shipCtrlMstDto.setCertificationOfOriginReport(i.getShCtrlCertOriginRep());
				shipCtrlMstDto.setSoldToMessrs(i.getShCtrlSoldMessrs());
				shipCtrlMstDto.setPlsFlag(i.getShCtrlPlsFlag());
				shipCtrlMstDto.setUpdateDateTime(i.getShCtrlUpdDt());
				
				shipCtrlMstDtos.add(shipCtrlMstDto);
			});
		}
		oemShippingCtrlMstDto.setMasterList(shipCtrlMstDtos);
		
		return oemShippingCtrlMstDto;
	}

	@Override
	public ConsigneeAndNotifyPartyDto fetchConsigneeAndNotifyPartyByBuyer(String buyer) {
		List<OemCnshMst> oemCnshMstList = oemCnshMstRepository.findAllByIdCmpCdOrderByIdCmpBranchAsc(buyer);
		List<CommonMultiSelectDropdownDto> shipCtrlConsigneeAndNotifyPartyDtoList = new ArrayList<>();
		oemCnshMstList.stream().forEach(c -> shipCtrlConsigneeAndNotifyPartyDtoList.add(new CommonMultiSelectDropdownDto(c.getId().getCmpBranch(), c.getId().getCmpBranch() + "-" + c.getCmpName())));
		List<CommonMultiSelectDropdownDto> shipCtrlConsigneeAndNotifyPartyList = shipCtrlConsigneeAndNotifyPartyDtoList.stream().distinct()
				.collect(Collectors.toList());
		
		return new ConsigneeAndNotifyPartyDto(shipCtrlConsigneeAndNotifyPartyList, shipCtrlConsigneeAndNotifyPartyList);
	}

	@Override
	public boolean saveShippingControlMaster(
			List<OemShippingCtrlMstSaveRequestDto> oemShippingCtrlMstSaveRequestDtoList) {
		
		List<OemShippingCtrlMstEntity> oemShippingCtrlMstEntities = new ArrayList<>();
		for (OemShippingCtrlMstSaveRequestDto oemShippingCtrlMstSaveRequestDto: oemShippingCtrlMstSaveRequestDtoList) {
			OemShippingCtrlMstEntity oemShippingCtrlMstEntity = new OemShippingCtrlMstEntity();
			
			//Set primary key fields
			OemShippingCtrlMstIdEntity oemShippingCtrlMstIdEntity = new OemShippingCtrlMstIdEntity();
			oemShippingCtrlMstIdEntity.setShCtrlCfCd(oemShippingCtrlMstSaveRequestDto.getCfcCode());
			oemShippingCtrlMstIdEntity.setShCtrlExpDstCd(oemShippingCtrlMstSaveRequestDto.getExpCode());
			oemShippingCtrlMstIdEntity.setShCtrlImpDstCd(oemShippingCtrlMstSaveRequestDto.getImpCode());
			oemShippingCtrlMstIdEntity.setShCtrlPmCd(oemShippingCtrlMstSaveRequestDto.getPortOfDischarge());
			oemShippingCtrlMstIdEntity.setShCtrlSetPartCd(oemShippingCtrlMstSaveRequestDto.getSetPartCode());
			oemShippingCtrlMstIdEntity.setShCtrlSrsName(oemShippingCtrlMstSaveRequestDto.getSeries());
			
			//Set primary key to OemShippingCtrlMstEntity
			oemShippingCtrlMstEntity.setId(oemShippingCtrlMstIdEntity);
			
			if ("Y".equals(oemShippingCtrlMstSaveRequestDto.getIsNewRow()) && !oemShippingCtrlMstRepository.findById(oemShippingCtrlMstIdEntity).isEmpty()) {
				Map<String, Object> errorMessageParams = new HashMap<>();
				StringBuilder errorMessage = new StringBuilder("Imp. Code ").append(oemShippingCtrlMstSaveRequestDto.getImpCode())
						.append(",Exp. Code ").append(oemShippingCtrlMstSaveRequestDto.getExpCode())
						.append(",CFC Code ").append(oemShippingCtrlMstSaveRequestDto.getCfcCode())
						.append(",Series ").append(oemShippingCtrlMstSaveRequestDto.getSeries())
						.append(",Set-Part Code ").append(oemShippingCtrlMstSaveRequestDto.getSetPartCode())
						.append(",Port of Discharge ").append(oemShippingCtrlMstSaveRequestDto.getPortOfDischarge());
				errorMessageParams.put("keyColumns", errorMessage.toString());
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3008, errorMessageParams);  
			}
			
			//Set other fields
			oemShippingCtrlMstEntity.setShCtrBuyer(oemShippingCtrlMstSaveRequestDto.getBuyer());
			oemShippingCtrlMstEntity.setShCtrlCertOriginRep(oemShippingCtrlMstSaveRequestDto.getCertificationOfOriginReport());
			oemShippingCtrlMstEntity.setShCtrlCnsg(oemShippingCtrlMstSaveRequestDto.getConsignee());
			oemShippingCtrlMstEntity.setShCtrlFolderNm(oemShippingCtrlMstSaveRequestDto.getFolderName());
			oemShippingCtrlMstEntity.setShCtrlGoodsDesc1(oemShippingCtrlMstSaveRequestDto.getGoodDesc1());
			oemShippingCtrlMstEntity.setShCtrlGoodsDesc2(oemShippingCtrlMstSaveRequestDto.getGoodDesc2());
			oemShippingCtrlMstEntity.setShCtrlGoodsDesc3(oemShippingCtrlMstSaveRequestDto.getGoodDesc3());
			oemShippingCtrlMstEntity.setShCtrlNtfPartyDtls(oemShippingCtrlMstSaveRequestDto.getNotifyParty());
			oemShippingCtrlMstEntity.setShCtrlPlsFlag(oemShippingCtrlMstSaveRequestDto.getPlsFlag());
			oemShippingCtrlMstEntity.setShCtrlProdGrpCd(oemShippingCtrlMstSaveRequestDto.getProductGroup());
			oemShippingCtrlMstEntity.setShCtrlSoldMessrs(oemShippingCtrlMstSaveRequestDto.getSoldToMessrs());
			oemShippingCtrlMstEntity.setShCtrlTradeTermCd(oemShippingCtrlMstSaveRequestDto.getTradeTerm());
			oemShippingCtrlMstEntity.setShCtrlUpdBy(oemShippingCtrlMstSaveRequestDto.getUpdateByUserId());
			oemShippingCtrlMstEntity.setShCtrlUpdDt(Timestamp.valueOf(LocalDateTime.now()));
			
			//Add record to list
			oemShippingCtrlMstEntities.add(oemShippingCtrlMstEntity);
		}
		
		try {
			//Save all records
			return !oemShippingCtrlMstRepository.saveAll(oemShippingCtrlMstEntities).isEmpty();
		} catch(Exception e) {
			return false;
		}
	}

	@Override
	public void deleteShippingControlMaster(
			List<OemShippingCtrlMstDeleteRequestDto> oemShippingCtrlMstDeleteRequestDtoList) {
		
		//Map DTO to Entity
		List<OemShippingCtrlMstIdEntity> oemShippingCtrlMstIdEntities = oemShippingCtrlMstDeleteRequestDtoList.stream()
				.map(s -> new OemShippingCtrlMstIdEntity(s.getCfcCode(), s.getImpCode(), s.getExpCode(),
						s.getSetPartCode(), s.getSeries(), s.getPortOfDischarge())).collect(Collectors.toList());
		//Delete all records by id
		oemShippingCtrlMstRepository.deleteAllById(oemShippingCtrlMstIdEntities);
	}

}
