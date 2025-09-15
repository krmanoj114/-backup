package com.tpex.invoice.serviceImpl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.InsInvPartsDetailsEntity;
import com.tpex.entity.InsInvPartsDetailsIdEntity;
import com.tpex.entity.InvModuleDetailsEntity;
import com.tpex.entity.InvModuleDetailsIdEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceNumberResponseDTO;
import com.tpex.invoice.dto.InvoiceReCalculateRequestDto;
import com.tpex.invoice.dto.InvoiceReCalculationResponseDTO;
import com.tpex.invoice.dto.InvoiceRecalculatePart;
import com.tpex.invoice.serviceimpl.InvoiceRecalculateServiceImpl;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.InvModuleDetailsRepository;

@ExtendWith(MockitoExtension.class)
class InvoiceRecalculateServiceImplTest {

	@Mock
	InsInvDetailsRepository insInvDetailsRepository;
	
	@Mock
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;
	
	@Mock
	InvModuleDetailsRepository invModuleDetailsRepository;
	
	@InjectMocks
	InvoiceRecalculateServiceImpl invoiceRecalculateServiceImpl;
	
	private static String invoiceNo = "KC22102079";
	private static String partNo = "426520K85300";
	private static String companyCode = "TMT";
	private static String userId = "TestUser";
	private static String privilegePr = "PR";
	private static String privilegePp = "PP";
	private static String privilegePn = "PN";
	private static String privilegePw = "PW";
	private static String etd = "2022-12-12";
	private static String eta = "2022-12-16";
	private static String cfCode = "889W";
	private static String cfSeries = "HILUX";
	private static String lot = "***";
	private static String modNo = "BHC374";
	private static String boxNo = "001";
	
	@Test
	void getDetailsByInvNo() throws ParseException {
		
		InvoiceNumberResponseDTO response = new InvoiceNumberResponseDTO();
		
		Optional<InsInvDtlsEntity> insInvDtlsEntity = Optional.empty();
		
		/*
		 * insInvDtlsEntity.setIndInvNo(invoiceNo); insInvDtlsEntity.setIndInvTyp("");
		 * insInvDtlsEntity.setCompanyCode(companyCode);
		 * insInvDtlsEntity.setIndEtd(null); insInvDtlsEntity.setIndEta(null);
		 * insInvDtlsEntity.setIndFinalDst("812B"); insInvDtlsEntity.setIndBuyer("UMW");
		 * insInvDtlsEntity.setIndPayCrncy("USD");
		 */
		
		List<InsInvPartsDetailsEntity> listOfEntity = new ArrayList<>();
		
		InsInvPartsDetailsIdEntity InsInvPartsDetailsIdEntity = new InsInvPartsDetailsIdEntity();
		InsInvPartsDetailsIdEntity.setInpInvNo(invoiceNo);
		InsInvPartsDetailsIdEntity.setInpBoxNo("001");
		InsInvPartsDetailsIdEntity.setInpLotNo("***");
		InsInvPartsDetailsIdEntity.setInpModNo("BHC374");
		InsInvPartsDetailsIdEntity.setInpPartNo(partNo);
		InsInvPartsDetailsEntity InsInvPartsDetailsEntity = new InsInvPartsDetailsEntity();
		InsInvPartsDetailsEntity.setCompanyCode(companyCode);
		InsInvPartsDetailsEntity.setId(null);
		listOfEntity.add(InsInvPartsDetailsEntity);
		Set<CommonMultiSelectDropdownDto> partNumbers = new HashSet<>();
		
		Mockito.lenient().when(insInvDetailsRepository.findByIndInvNoAndCompanyCode(invoiceNo,companyCode)).thenReturn(insInvDtlsEntity);
		InvoiceNumberResponseDTO detailsByInvNo = invoiceRecalculateServiceImpl.getDetailsByInvNo(invoiceNo, companyCode);
		assertNotNull(detailsByInvNo);
	}
	
	@Test
	void testGetInvRecalculateDetails() throws ParseException {
		
		List<Object[]> listOfObjects = new ArrayList<>();
		
		Object[] objs = new Object[] { "Y", cfCode, cfSeries, partNo, "TIRE, TUBELESS", lot, "202211", "4", "1", "invOrig", "orig", "invHS", "HS" };
		listOfObjects.add(objs);
		
		List<InvoiceReCalculationResponseDTO> finalResponse = new ArrayList<>();
		
		InvoiceReCalculationResponseDTO response = new InvoiceReCalculationResponseDTO();
		response.setFlag("Y");
		response.setCfCode(cfCode);
		response.setCfSeries(cfSeries);
		response.setPartNo(partNo);
		response.setPartName("TIRE, TUBELESS");
		response.setLot(lot);
		response.setPakageMonth("202211");
		response.setInvAico("4");
		response.setIxosAico("1");
		response.setIncOriginCriteria(null);
		response.setIxosOriginCriteria(null);
		response.setInvHSCode(null);
		response.setIxosHSCode(null);
		finalResponse.add(response);
		
		List<String> list = new ArrayList<>();
		list.add("426520K85300");
		when(insInvPartsDetailsRepository.getInvPartDetails(invoiceNo, partNo, privilegePr, companyCode)).thenReturn(listOfObjects);
		List<InvoiceReCalculationResponseDTO> invRecalculateDetails = invoiceRecalculateServiceImpl.getInvRecalculateDetails(invoiceNo, list, privilegePr, companyCode);
		when(insInvPartsDetailsRepository.getInvPartDetails(invoiceNo, partNo, privilegePp, companyCode)).thenReturn(listOfObjects);
		List<InvoiceReCalculationResponseDTO> invRecalculateDetails1 = invoiceRecalculateServiceImpl.getInvRecalculateDetails(invoiceNo, list, privilegePp, companyCode);
		when(insInvPartsDetailsRepository.getInvPartDetails(invoiceNo, partNo, privilegePn, companyCode)).thenReturn(listOfObjects);
		List<InvoiceReCalculationResponseDTO> invRecalculateDetails2 = invoiceRecalculateServiceImpl.getInvRecalculateDetails(invoiceNo, list, privilegePn, companyCode);
		when(insInvPartsDetailsRepository.getInvPartDetails(invoiceNo, partNo, privilegePw, companyCode)).thenReturn(listOfObjects);
		List<InvoiceReCalculationResponseDTO> invRecalculateDetails3 = invoiceRecalculateServiceImpl.getInvRecalculateDetails(invoiceNo, list, privilegePw, companyCode);

		assertNotNull(invRecalculateDetails);
		assertNotNull(invRecalculateDetails1);
		assertNotNull(invRecalculateDetails2);
		assertNotNull(invRecalculateDetails3);
	}
	
	@Test
	void testRecalculateInvoicePP() {
		List<Map<String, Object>> invPartList = new ArrayList<>();
		Map<String, Object> invPartMap = new HashMap<>();
		invPartMap.put("INV_NO", invoiceNo);
		invPartMap.put("MOD_NO", modNo);
		invPartMap.put("LOT_NO", lot);
		invPartMap.put("PART_NO", partNo);
		invPartMap.put("BOX_NO", boxNo);
		invPartMap.put("PART_PRICE", 20.00);
		invPartList.add(invPartMap);
		
		Mockito.when(insInvDetailsRepository.getInvPartsForPartPriceUpdate(Mockito.any(), Mockito.any())).thenReturn(invPartList);
		
		InsInvPartsDetailsEntity insInvPartsDetailsEntity = new InsInvPartsDetailsEntity();
		insInvPartsDetailsEntity.setId(new InsInvPartsDetailsIdEntity(invoiceNo, modNo, lot, partNo, boxNo));
		insInvPartsDetailsEntity.setInpUnitPerBox(10.00);
		insInvPartsDetailsEntity.setInpPrc(20.00);
		
		Mockito.when(insInvPartsDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(insInvPartsDetailsEntity));

		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
		Mockito.when(insInvPartsDetailsRepository.saveAll(Mockito.any())).thenReturn(insInvPartsDetailsEntityList);
		
		InsInvDtlsEntity insInvDtlsEntity = new InsInvDtlsEntity();
		insInvDtlsEntity.setIndInvNo(invoiceNo);
		Mockito.when(insInvDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(insInvDtlsEntity));
		
		Mockito.when(insInvDetailsRepository.save(Mockito.any())).thenReturn(insInvDtlsEntity);

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId(userId);
		invoiceReCalculateRequestDto.setPrivilege(privilegePp);
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart(partNo, "16", "4.00", "2.00", "300.00", "200.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		
        assertDoesNotThrow(() -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto));

	}
	
	@Test
	void testRecalculateInvoicePN() {
		List<Map<String, Object>> invPartList = new ArrayList<>();
		Map<String, Object> invPartMap = new HashMap<>();
		invPartMap.put("INV_NO", invoiceNo);
		invPartMap.put("MOD_NO", modNo);
		invPartMap.put("LOT_NO", lot);
		invPartMap.put("PART_NO", partNo);
		invPartMap.put("BOX_NO", boxNo);
		invPartMap.put("PART_NAME", "BRACKET SUB-ASSY, ENGINE FR MOUNTING, RH");
		invPartList.add(invPartMap);
		
		Mockito.when(insInvDetailsRepository.getInvPartsForPartNameUpdate(Mockito.any(), Mockito.any())).thenReturn(invPartList);
		
		InsInvPartsDetailsEntity insInvPartsDetailsEntity = new InsInvPartsDetailsEntity();
		insInvPartsDetailsEntity.setId(new InsInvPartsDetailsIdEntity(invoiceNo, modNo, lot, partNo, boxNo));
		
		Mockito.when(insInvPartsDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(insInvPartsDetailsEntity));

		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
		Mockito.when(insInvPartsDetailsRepository.saveAll(Mockito.any())).thenReturn(insInvPartsDetailsEntityList);
		
		InsInvDtlsEntity insInvDtlsEntity = new InsInvDtlsEntity();
		insInvDtlsEntity.setIndInvNo(invoiceNo);
		Mockito.when(insInvDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(insInvDtlsEntity));
		
		Mockito.when(insInvDetailsRepository.save(Mockito.any())).thenReturn(insInvDtlsEntity);

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId(userId);
		invoiceReCalculateRequestDto.setPrivilege(privilegePn);
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart(partNo, "16", "4.00", "2.00", "300.00", "200.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		
        assertDoesNotThrow(() -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto));

	}
	
	
	@Test
	void testRecalculateInvoicePW() {
				
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		InsInvPartsDetailsEntity insInvPartsDetailsEntity = new InsInvPartsDetailsEntity();
		insInvPartsDetailsEntity.setId(new InsInvPartsDetailsIdEntity(invoiceNo, modNo, lot, partNo, boxNo));
		insInvPartsDetailsEntity.setInpUnitPerBox(16.00);
		insInvPartsDetailsEntity.setInpNetWt(4.00);
		insInvPartsDetailsEntity.setInpBoxGrossWt(64.00);
		insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
		
		Mockito.when(insInvPartsDetailsRepository.findByIdInpInvNoAndIdInpPartNo(Mockito.any(), Mockito.any())).thenReturn(insInvPartsDetailsEntityList);

		Mockito.when(insInvPartsDetailsRepository.saveAll(Mockito.any())).thenReturn(insInvPartsDetailsEntityList);
		
		List<Map<String, Object>> invModList = new ArrayList<>();
		Map<String, Object> invModMap = new HashMap<>();
		invModMap.put("INV_NO", invoiceNo);
		invModMap.put("MOD_NO", modNo);
		invModMap.put("LOT_NO", lot);
		invModMap.put("GROSS_WT", 64.00);
		invModList.add(invModMap);
		
		Mockito.when(invModuleDetailsRepository.getInvModuleForGrossWtUpdate(Mockito.any())).thenReturn(invModList);

		InvModuleDetailsEntity invModuleDetailsEntity = new InvModuleDetailsEntity();
		invModuleDetailsEntity.setId(new InvModuleDetailsIdEntity(invoiceNo, modNo, lot));
		invModuleDetailsEntity.setGrossWt(64.00);
		Mockito.when(invModuleDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(invModuleDetailsEntity));
		
		List<InvModuleDetailsEntity> invModuleDetailsEntityList = new ArrayList<>();
		invModuleDetailsEntityList.add(invModuleDetailsEntity);
		Mockito.when(invModuleDetailsRepository.saveAll(Mockito.any())).thenReturn(invModuleDetailsEntityList);
		
		InsInvDtlsEntity insInvDtlsEntity = new InsInvDtlsEntity();
		insInvDtlsEntity.setIndInvNo(invoiceNo);
		Mockito.when(insInvDetailsRepository.findById(Mockito.any())).thenReturn(Optional.of(insInvDtlsEntity));
		
		Mockito.when(insInvDetailsRepository.save(Mockito.any())).thenReturn(insInvDtlsEntity);

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId(userId);
		invoiceReCalculateRequestDto.setPrivilege(privilegePw);
		List<InvoiceRecalculatePart> invoiceRecalculateParts = new ArrayList<>();
		invoiceRecalculateParts.add(new InvoiceRecalculatePart(partNo, "16", "4.00", "5.00", "64.00", "64.00"));
		invoiceReCalculateRequestDto.setPartDetails(invoiceRecalculateParts);
		
        assertDoesNotThrow(() -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto));
        
        InvoiceReCalculateRequestDto invoiceReCalculateRequestDto1 = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto1.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto1.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto1.setUserId(userId);
		invoiceReCalculateRequestDto1.setPrivilege(privilegePw);
		List<InvoiceRecalculatePart> invoiceRecalculateParts1 = new ArrayList<>();
		invoiceRecalculateParts1.add(new InvoiceRecalculatePart(partNo, "16", "4.00", "", "64.00", "60.00"));
		invoiceReCalculateRequestDto1.setPartDetails(invoiceRecalculateParts1);
		
        assertDoesNotThrow(() -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto1));

	}
	
	@Test
	void testRecalculateInvoicePR() {
		
		Mockito.when(insInvDetailsRepository.reCalculateInvoicePrivilage(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn("");

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId(userId);
		invoiceReCalculateRequestDto.setPrivilege(privilegePr);
		
        assertDoesNotThrow(() -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto));

	}
	
	@Test
	void testRecalculateInvoicePRException() {
		
		Mockito.when(insInvDetailsRepository.reCalculateInvoicePrivilage(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(partNo);

		InvoiceReCalculateRequestDto invoiceReCalculateRequestDto = new InvoiceReCalculateRequestDto();
		invoiceReCalculateRequestDto.setInvoiceNumber(invoiceNo);
		invoiceReCalculateRequestDto.setCompanyCode(companyCode);
		invoiceReCalculateRequestDto.setUserId(userId);
		invoiceReCalculateRequestDto.setPrivilege(privilegePr);
		
		assertThrows(MyResourceNotFoundException.class, () -> invoiceRecalculateServiceImpl.recalculateInvoice(invoiceReCalculateRequestDto));

	}
	
}

