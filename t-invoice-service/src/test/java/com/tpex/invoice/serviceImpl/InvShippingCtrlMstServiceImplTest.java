package com.tpex.invoice.serviceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.dto.ConsigneeAndNotifyPartyDto;
import com.tpex.dto.OemShippingCtrlMstDeleteRequestDto;
import com.tpex.dto.OemShippingCtrlMstSaveRequestDto;
import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyDestinationMasterIdEntity;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.InsProdGrpMstEntity;
import com.tpex.entity.OemCnshIdMstEntity;
import com.tpex.entity.OemCnshMst;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.entity.OemShippingCtrlMstEntity;
import com.tpex.entity.OemShippingCtrlMstIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.OemShippingCtrlMstDto;
import com.tpex.invoice.serviceimpl.InvShippingCtrlMstServiceImpl;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.InsProdGrpMstRepository;
import com.tpex.repository.OemCnshMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.OemShippingCtrlMstRepository;
import com.tpex.util.ConstantProperties;
import com.tpex.util.TpexConfigurationUtil;

@ExtendWith(MockitoExtension.class)
 class InvShippingCtrlMstServiceImplTest {

	@InjectMocks
	private InvShippingCtrlMstServiceImpl invShippingCtrlMstServiceImpl;
	
	@Mock
	private OemShippingCtrlMstRepository oemShippingCtrlMstRepository;
	
	@Mock
	private ConstantProperties constantProperties;
	
	@Mock
	private OemCnshMstRepository oemCnshMstRepository;
	
	@Mock
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@Mock
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@Mock
	private OemPortMstRepository oemPortMstRepository;
	
	@Mock
	private InsProdGrpMstRepository insProdGrpMstRepository;
	
	@Mock
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	@Mock
	private CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;
	
	@Test
	void fetchInvShippingCtrlMstTest() throws Exception {
		List<OemShippingCtrlMstEntity> oemShippingCtrlMstEntities = new ArrayList<>();
		OemShippingCtrlMstEntity oemShippingCtrlMstEntity = new OemShippingCtrlMstEntity();
		oemShippingCtrlMstEntity.setShCtrBuyer("TMT");
		oemShippingCtrlMstEntity.setId(new OemShippingCtrlMstIdEntity("000A", "722E", "465K", "P", "TOYOTA", "BTG"));
		oemShippingCtrlMstEntity.setShCtrlProdGrpCd("CL");
		oemShippingCtrlMstEntity.setShCtrlFolderNm("Non-IMV");
		oemShippingCtrlMstEntity.setShCtrlCnsg("1");
		oemShippingCtrlMstEntity.setShCtrlNtfPartyDtls("1");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc1("Toyota Component Parts");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc2("For Model : VIOS");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc3("For Detail See Attachment");
		oemShippingCtrlMstEntity.setShCtrlTradeTermCd("CIF");
		oemShippingCtrlMstEntity.setShCtrlCertOriginRep("Y");
		oemShippingCtrlMstEntity.setShCtrlSoldMessrs("TMX");
		oemShippingCtrlMstEntity.setShCtrlPlsFlag("Y");
		oemShippingCtrlMstEntity.setShCtrlUpdBy("Test");
		oemShippingCtrlMstEntities.add(oemShippingCtrlMstEntity);
		
		Mockito.when(oemShippingCtrlMstRepository.findAllByOrderByIdShCtrlImpDstCdAscIdShCtrlCfCdAsc()).thenReturn(oemShippingCtrlMstEntities);

		//Mock buyer list
		List<OemCnshMst> oemCnshMstList = new ArrayList<>();
		OemCnshMst oemCnshMst1 = new OemCnshMst();
		oemCnshMst1.setId(new OemCnshIdMstEntity("TMX", "TMX-TOYOTA MOTOR ASIA PACIFIC PTE, LTD.", "1"));
		oemCnshMstList.add(oemCnshMst1);
		
		OemCnshMst oemCnshMst2 = new OemCnshMst();
		oemCnshMst2.setId(new OemCnshIdMstEntity("TMX", "TMI-PT.TOYOTA MOTOR MANUFACTURING INDONESIA", "2"));
		oemCnshMstList.add(oemCnshMst2);
		
		Mockito.when(oemCnshMstRepository.findAllByOrderByIdCmpCdAsc()).thenReturn(oemCnshMstList);
		
		List<OemFnlDstMstEntity> oemFnlDstMstEntityList = new ArrayList<>();
		OemFnlDstMstEntity oemFnlDstMstEntity1 = new OemFnlDstMstEntity();
		oemFnlDstMstEntity1.setFdDstCd("465K");
		oemFnlDstMstEntityList.add(oemFnlDstMstEntity1);
		
		OemFnlDstMstEntity oemFnlDstMstEntity2 = new OemFnlDstMstEntity();
		oemFnlDstMstEntity2.setFdDstCd("722E");
		oemFnlDstMstEntityList.add(oemFnlDstMstEntity2);
		
		Mockito.when(oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc()).thenReturn(oemFnlDstMstEntityList);

		List<CarFamilyMasterEntity> carFamilyMasterEntityList = new ArrayList<>();
		CarFamilyMasterEntity carFamilyMasterEntity1 = new CarFamilyMasterEntity();
		carFamilyMasterEntity1.setCarFmlyCode("000A");
		carFamilyMasterEntityList.add(carFamilyMasterEntity1);
		
		CarFamilyMasterEntity carFamilyMasterEntity2 = new CarFamilyMasterEntity();
		carFamilyMasterEntity2.setCarFmlyCode("436L");
		carFamilyMasterEntityList.add(carFamilyMasterEntity2);
		
		Mockito.when(carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc()).thenReturn(carFamilyMasterEntityList);

		List<CarFamilyMasterEntity> carFamilyMasterEntities = new ArrayList<>();
		CarFamilyMasterEntity carFamilyMasterEntity3 = new CarFamilyMasterEntity();
		carFamilyMasterEntity3.setCarFmlyCode("000A");
		carFamilyMasterEntity3.setCarFmlySrsName("TOYOTA");
		carFamilyMasterEntities.add(carFamilyMasterEntity1);
		
		CarFamilyMasterEntity carFamilyMasterEntity4 = new CarFamilyMasterEntity();
		carFamilyMasterEntity4.setCarFmlyCode("436L");
		carFamilyMasterEntity4.setCarFmlySrsName("FORTUNER");
		carFamilyMasterEntities.add(carFamilyMasterEntity2);
		
		Mockito.when(carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc()).thenReturn(carFamilyMasterEntities);

		List<OemPortMstEntity> oemPortMstEntityList = new ArrayList<>();
		OemPortMstEntity oemPortMstEntity1 = new OemPortMstEntity();
		oemPortMstEntity1.setCd("BTG");
		oemPortMstEntity1.setName("BTG-BATANGAS");
		oemPortMstEntityList.add(oemPortMstEntity1);
		
		OemPortMstEntity oemPortMstEntity2 = new OemPortMstEntity();
		oemPortMstEntity2.setCd("LAG");
		oemPortMstEntity2.setName("LAG-LA GUAIRA");
		oemPortMstEntityList.add(oemPortMstEntity2);
		
		Mockito.when(oemPortMstRepository.findAllByOrderByCdAsc()).thenReturn(oemPortMstEntityList);

		List<InsProdGrpMstEntity> insProdGrpMstEntityList = new ArrayList<>();
		InsProdGrpMstEntity insProdGrpMstEntity1 = new InsProdGrpMstEntity();
		insProdGrpMstEntity1.setIpgProdGrpCd("CL");
		insProdGrpMstEntity1.setIpgProdGrpDesc("CL-COROLLA");
		insProdGrpMstEntityList.add(insProdGrpMstEntity1);
		
		InsProdGrpMstEntity insProdGrpMstEntity2 = new InsProdGrpMstEntity();
		insProdGrpMstEntity2.setIpgProdGrpCd("SL");
		insProdGrpMstEntity2.setIpgProdGrpDesc("SL-VIOS");
		insProdGrpMstEntityList.add(insProdGrpMstEntity2);
		
		Mockito.when(insProdGrpMstRepository.findAllByOrderByIpgProdGrpCd()).thenReturn(insProdGrpMstEntityList);
		when(tpexConfigurationUtil.getFilePath(Mockito.any())).thenReturn("classpath:shipCtrlScreenTradeTermList.json");
		Mockito.when(oemCnshMstRepository.findAllByOrderByIdCmpBranchAsc()).thenReturn(oemCnshMstList);
		
		List<CarFamilyDestinationMasterEntity> carFamilyDestinationMasterEntityList= new ArrayList<>();
		
		CarFamilyDestinationMasterIdEntity carFamilyDestinationMasterIdEntity = new CarFamilyDestinationMasterIdEntity();
		carFamilyDestinationMasterIdEntity.setDestinationCode("807D");
		carFamilyDestinationMasterIdEntity.setReExporterCode("4");
		carFamilyDestinationMasterIdEntity.setCarFmlyCode("272W");
		
		
		CarFamilyDestinationMasterEntity carFamilyDestinationMasterEntity = new CarFamilyDestinationMasterEntity();
		carFamilyDestinationMasterEntity.setCompanyCode("TMT");
		carFamilyDestinationMasterEntity.setId(carFamilyDestinationMasterIdEntity);
		carFamilyDestinationMasterEntity.setSrsName("FORTUNER");
		carFamilyDestinationMasterEntity.setUpdatedBy("SUKUMAL");
		
		carFamilyDestinationMasterEntityList.add(carFamilyDestinationMasterEntity);
		Mockito.when(carFamilyDestinationMasterRepository.findAllByOrderBySrsNameAsc()).thenReturn(carFamilyDestinationMasterEntityList);

        OemShippingCtrlMstDto oemShippingCtrlMstDto = invShippingCtrlMstServiceImpl.fetchInvShippingCtrlMst();
        assertThat(oemShippingCtrlMstDto).isNotNull();

	}
	
	@Test
	void fetchConsigneeAndNotifyPartyByBuyerTest() throws Exception {
		
		List<OemCnshMst> oemCnshMstList = new ArrayList<>();
		OemCnshMst oemCnshMst1 = new OemCnshMst();
		oemCnshMst1.setId(new OemCnshIdMstEntity("TMI", "TMI-TOYOTA MOTOR ASIA PACIFIC PTE, LTD.", "1"));
		oemCnshMst1.setCmpName("TOYOTA MOTOR ASIA PACIFIC PTE, LTD.");
		oemCnshMstList.add(oemCnshMst1);
		
		OemCnshMst oemCnshMst2 = new OemCnshMst();
		oemCnshMst2.setId(new OemCnshIdMstEntity("TMI", "TMI-PT.TOYOTA MOTOR MANUFACTURING INDONESIA", "2"));
		oemCnshMst2.setCmpName("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		oemCnshMstList.add(oemCnshMst2);
		
		Mockito.when(oemCnshMstRepository.findAllByIdCmpCdOrderByIdCmpBranchAsc(Mockito.anyString())).thenReturn(oemCnshMstList);
		
        ConsigneeAndNotifyPartyDto consigneeAndNotifyPartyDtoResult = invShippingCtrlMstServiceImpl.fetchConsigneeAndNotifyPartyByBuyer("TMI");

        List<CommonMultiSelectDropdownDto> consigneeList = new ArrayList<>();
        consigneeList.add(new CommonMultiSelectDropdownDto("1", "1-TOYOTA MOTOR ASIA PACIFIC PTE, LTD."));
        consigneeList.add(new CommonMultiSelectDropdownDto("2", "2-PT.TOYOTA MOTOR MANUFACTURING INDONESIA"));
        
    	List<CommonMultiSelectDropdownDto> notifyPartyList = new ArrayList<>();
    	notifyPartyList.add(new CommonMultiSelectDropdownDto("1", "1-TOYOTA MOTOR ASIA PACIFIC PTE, LTD."));
    	notifyPartyList.add(new CommonMultiSelectDropdownDto("2", "2-PT.TOYOTA MOTOR MANUFACTURING INDONESIA"));
    	
        assertThat(consigneeAndNotifyPartyDtoResult).isNotNull();
        assertEquals(new ConsigneeAndNotifyPartyDto(consigneeList, notifyPartyList), consigneeAndNotifyPartyDtoResult);

	}
	
	@Test
	void saveShippingControlMasterTest() {
		
		List<OemShippingCtrlMstEntity> oemShippingCtrlMstEntities = new ArrayList<>();
		
		OemShippingCtrlMstEntity oemShippingCtrlMstEntity = new OemShippingCtrlMstEntity();
		oemShippingCtrlMstEntity.setShCtrBuyer("TMT");
		oemShippingCtrlMstEntity.setId(new OemShippingCtrlMstIdEntity("000A", "722E", "465K", "P", "TOYOTA", "BTG"));
		oemShippingCtrlMstEntity.setShCtrlProdGrpCd("CL");
		oemShippingCtrlMstEntity.setShCtrlFolderNm("Non-IMV");
		oemShippingCtrlMstEntity.setShCtrlCnsg("1");
		oemShippingCtrlMstEntity.setShCtrlNtfPartyDtls("1");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc1("Toyota Component Parts");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc2("For Model : VIOS");
		oemShippingCtrlMstEntity.setShCtrlGoodsDesc3("For Detail See Attachment");
		oemShippingCtrlMstEntity.setShCtrlTradeTermCd("CIF");
		oemShippingCtrlMstEntity.setShCtrlCertOriginRep("Y");
		oemShippingCtrlMstEntity.setShCtrlSoldMessrs("TMX");
		oemShippingCtrlMstEntity.setShCtrlPlsFlag("Y");
		oemShippingCtrlMstEntity.setShCtrlUpdBy("Test");
		
		oemShippingCtrlMstEntities.add(oemShippingCtrlMstEntity);
		
		Mockito.when(oemShippingCtrlMstRepository.saveAll(Mockito.anyIterable())).thenReturn(oemShippingCtrlMstEntities);
		
		List<OemShippingCtrlMstSaveRequestDto> oemShippingCtrlMstSaveRequestDtoList = new ArrayList<>();
		OemShippingCtrlMstSaveRequestDto oemShippingCtrlMstSaveRequestDto = new OemShippingCtrlMstSaveRequestDto();
		oemShippingCtrlMstSaveRequestDto.setBuyer("TMT");
		oemShippingCtrlMstSaveRequestDto.setImpCode("722E");
		oemShippingCtrlMstSaveRequestDto.setExpCode("465K");
		oemShippingCtrlMstSaveRequestDto.setCfcCode("000A");
		oemShippingCtrlMstSaveRequestDto.setSeries("TOYOTA");
		oemShippingCtrlMstSaveRequestDto.setSetPartCode("P");
		oemShippingCtrlMstSaveRequestDto.setPortOfDischarge("BTG");
		oemShippingCtrlMstSaveRequestDto.setProductGroup("CL");
		oemShippingCtrlMstSaveRequestDto.setFolderName("Non-IMV");
		oemShippingCtrlMstSaveRequestDto.setConsignee("1");
		oemShippingCtrlMstSaveRequestDto.setNotifyParty("1");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc1("Toyota Component Parts");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc2("For Model : VIOS");
		oemShippingCtrlMstSaveRequestDto.setGoodDesc3("For Detail See Attachment");
		oemShippingCtrlMstSaveRequestDto.setTradeTerm("CIF");
		oemShippingCtrlMstSaveRequestDto.setCertificationOfOriginReport("Y");
		oemShippingCtrlMstSaveRequestDto.setSoldToMessrs("TMX");
		oemShippingCtrlMstSaveRequestDto.setPlsFlag("Y");
		oemShippingCtrlMstSaveRequestDto.setUpdateByUserId("Test");
		oemShippingCtrlMstSaveRequestDto.setIsNewRow("Y");
		
		oemShippingCtrlMstSaveRequestDtoList.add(oemShippingCtrlMstSaveRequestDto);
		
        boolean isSaved = invShippingCtrlMstServiceImpl.saveShippingControlMaster(oemShippingCtrlMstSaveRequestDtoList);

        assertEquals(Boolean.TRUE, isSaved);
        
        Mockito.when(oemShippingCtrlMstRepository.findById(Mockito.any(OemShippingCtrlMstIdEntity.class))).thenReturn(Optional.of(oemShippingCtrlMstEntity));

        assertThrows(InvalidInputParametersException.class, () -> {invShippingCtrlMstServiceImpl.saveShippingControlMaster(oemShippingCtrlMstSaveRequestDtoList);});

	}
	
	@Test
	void deleteShippingControlMasterTest() {
		
		doNothing().when(oemShippingCtrlMstRepository).deleteAllById(Mockito.anyIterable());
		
		List<OemShippingCtrlMstDeleteRequestDto> oemShippingCtrlMstDeleteRequestDtos = new ArrayList<>();
		
		oemShippingCtrlMstDeleteRequestDtos.add(new OemShippingCtrlMstDeleteRequestDto("000A", "722E", "465K", "P", "TOYOTA", "BTG"));
		
        Assertions.assertDoesNotThrow(() -> invShippingCtrlMstServiceImpl.deleteShippingControlMaster(oemShippingCtrlMstDeleteRequestDtos));

	}
}
