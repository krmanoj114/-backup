package com.tpex.invoice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.Query;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.InsProdGrpMstEntity;
import com.tpex.entity.OemCnshIdMstEntity;
import com.tpex.entity.OemCnshMst;
import com.tpex.entity.OemCurrencyMstEntity;
import com.tpex.entity.OemPmntTermMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailResponseDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;
import com.tpex.invoice.serviceImpl.InvSearchServiceImpl;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.InsProdGrpMstRepository;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.repository.NoemRenbanSetupMstRepository;
import com.tpex.repository.OemCnshMstRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemPmntTermMstRepository;
import com.tpex.repository.OemPortMstRepository;

@ExtendWith(MockitoExtension.class)
class InvSearchServiceImplTest {

	@InjectMocks
	InvSearchServiceImpl invSearchServiceImpl;

	@Mock
	NoemRenbanSetupMstRepository noemRenbanSetupMstRepository;

	@Mock
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;

	@Mock
	OemPortMstRepository oemPortMstRepository;
	
	@Mock
	InsInvDetailsRepository insInvDetailsRepository;

	@Mock
	InsProdGrpMstRepository insProdGrpMstRepository;

	@Mock
	OemCnshMstRepository oemCnshMstRepository;

	@Mock
	OemCurrencyMstRepository oemCurrencyMstRepository;

	@Mock
	OemPmntTermMstRepository oemPmntTermMstRepository;
	
	@Mock
	EntityManager entityManager;
	
	@Mock
	Query query;

	@Test
	void testfetchHaisenDetail() throws Exception {
		List<Object[]> response = new ArrayList<>();
		//when(noemHaisenDtlsRepository.getHaisenDetails(LocalDate.of(2004, 11, 14), LocalDate.of(2004, 11, 14), Mockito.anyString(),
			//	Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(response);
		List<OemPortMstEntity> emPortMstObj = new ArrayList<>();
		//when(oemPortMstRepository.findAll()).thenReturn(emPortMstObj);
		SearchInvHaisenDetailResponseDto dto = new SearchInvHaisenDetailResponseDto();
		dto.setBuyer("TSM");
		dto.setContainerEfficiency("1.00");
		dto.setEta("27/11/2004");
		dto.setEtd("22/11/2004");
		dto.setFeederVessel(new ArrayList<>());
		dto.setFeederVoyage(new ArrayList<>());
		dto.setHaisenNo("TH136");
		dto.setHaisenYearMonth("200411");
		dto.setNoOf20ftContainer(0);
		dto.setNoOf40ftContainer(7);
		dto.setOceanVessel("SHIMANAMI");
		dto.setOceanVoyage("299S");
		dto.setPortOfDischarge("LCH LAEM CHABANG");
		dto.setPortOfLoading("LCH LAEM CHABANG");
		//response.add(dto);
		
		OemPortMstEntity oemPortMstEntity1=new OemPortMstEntity();
		oemPortMstEntity1.setCd("LCH");
		oemPortMstEntity1.setName("LAEM CHABANG");
		
		OemPortMstEntity oemPortMstEntity2=new OemPortMstEntity();
		oemPortMstEntity2.setCd("LCH");
		oemPortMstEntity2.setName("LAEM CHABANG");
		emPortMstObj.add(oemPortMstEntity1);
		emPortMstObj.add(oemPortMstEntity2);
		
		when(noemHaisenDtlsRepository.getHaisenDetails(LocalDate.of(2004, 11, 27), LocalDate.now(), "TSM",
				"R","C","S")).thenReturn(response);
		when(oemPortMstRepository.findAll()).thenReturn(emPortMstObj);
		
		SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto = new SearchInvHaisenDetailRequestDto();
		searchInvHaisenDetailRequestDto.setBuyer("TSM");
		searchInvHaisenDetailRequestDto.setEtdFrom(Date.valueOf("2004-11-14"));

		invSearchServiceImpl.fetchHaisenDetail(searchInvHaisenDetailRequestDto, "R", "C", "S");
		
		assertEquals("LCH", emPortMstObj.get(0).getCd());
	}
	//@Test
	void testsearchByInvNo() throws Exception {
		
		InsInvDtlsEntity InsInvDtlsEntity1=new InsInvDtlsEntity();
		InsInvDtlsEntity1.setIndInvDt(new Timestamp(0));
		InsInvDtlsEntity1.setIndEtd(new Timestamp(0));
		InsInvDtlsEntity1.setIndEta(new Timestamp(0));
		InsInvDtlsEntity1.setIndInvTyp("4");
		InsInvDtlsEntity1.setIndOrdTyp("R");
		InsInvDtlsEntity1.setIndLotPttrn("P");
		
		
		Optional<InsInvDtlsEntity> insInvDtlsEntity=Optional.of(InsInvDtlsEntity1);
		
		Mockito.lenient().when(insInvDetailsRepository.findById("KR22102701")).thenReturn(insInvDtlsEntity);
		OemPortMstEntity oemPortMstEntity = new OemPortMstEntity();
		oemPortMstEntity.setCd("LCH");
		oemPortMstEntity.setName("LAEM CHABANG");
		Mockito.lenient().when(oemPortMstRepository.findBydepPortCd("LCH")).thenReturn(oemPortMstEntity);
		
		OemCurrencyMstEntity oemCurrencyMstEntity=new OemCurrencyMstEntity();
		oemCurrencyMstEntity.setCrmCd("USD");
		oemCurrencyMstEntity.setCrmDesc("DOLLARS");
		
		Optional<OemCurrencyMstEntity> oemCurrencyMstEntity1=Optional.of(oemCurrencyMstEntity);
		Mockito.lenient().when(oemCurrencyMstRepository.findById("USD")).thenReturn(oemCurrencyMstEntity1);
		
		List<OemPmntTermMstEntity> oemPmntTermMsObj=new ArrayList<>();
		Mockito.lenient().when(oemPmntTermMstRepository.findAll()).thenReturn(oemPmntTermMsObj);
		
		List<InsProdGrpMstEntity> insProdGrpMstEntity=new ArrayList<>();
		Mockito.lenient().when(insProdGrpMstRepository.findAll()).thenReturn(insProdGrpMstEntity);
		
		List<Object[]> authEmp=new ArrayList<>();
		Mockito.lenient().when(oemCnshMstRepository.getAuthEmp("TSM")).thenReturn(authEmp);
		
		invSearchServiceImpl.searchByInvNo("KR22102701");
		
	}

	void testgetInvoiceDetails() {

	}
	
	@Test
	void updateInvDetailsByInvNoTest() {
		InsInvDtlsEntity insInvDtlsEntity = new InsInvDtlsEntity();
		insInvDtlsEntity.setIndInvNo("KR22106826");
		insInvDtlsEntity.setIndCust("TMC");
		insInvDtlsEntity.setIndCustNm("TOYOTA MOTOR CORPORATION");
		insInvDtlsEntity.setIndBuyer("TMI");
		insInvDtlsEntity.setIndBuyerNm("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		insInvDtlsEntity.setIndNotifyName("MS. EXPRESS  S.A.  (PTY)  LTD.");
		insInvDtlsEntity.setIndPayTerm("71");
		insInvDtlsEntity.setIndScAuthEmp("MR. CHAVALIT VESARNSEATTAPUN");
		insInvDtlsEntity.setIndProdGrpCd("RHA-Description");
		
		insInvDtlsEntity.setIndMark1("UMW");
		insInvDtlsEntity.setIndMark2("722E/812B");
		insInvDtlsEntity.setIndMark3("SERIES : HILUX");
		insInvDtlsEntity.setIndMark4("EW0047-0052,EX0084-0089");
		insInvDtlsEntity.setIndMark5("GA,GB,SA,SB");
		insInvDtlsEntity.setIndMark7("PORT KLANG, MALAYSIA");
		insInvDtlsEntity.setIndMark8("Test Mark8");
		insInvDtlsEntity.setIndGoodsDesc1("COMPONENT PARTS FOR");
		insInvDtlsEntity.setIndGoodsDesc2("TOYOTA HILUX");
		insInvDtlsEntity.setIndGoodsDesc3("For Detail See Attachment");
		insInvDtlsEntity.setIndGoodsDesc4("Goods Desc 4");
		insInvDtlsEntity.setIndGoodsDesc5("Goods Desc 5");
		insInvDtlsEntity.setIndGoodsDesc6("Goods Desc 6");
		insInvDtlsEntity.setIndFreight(0.00);
		insInvDtlsEntity.setIndInsurance(0.00);
		
		Mockito.when(insInvDetailsRepository.findById(Mockito.anyString())).thenReturn(Optional.of(insInvDtlsEntity));	

		InsInvDtlsEntity insInvDtlsEntity1 = new InsInvDtlsEntity();
		insInvDtlsEntity1.setIndInvNo("KR22106826");
		insInvDtlsEntity1.setIndCust("TMC");
		insInvDtlsEntity1.setIndCustNm("TOYOTA MOTOR CORPORATION");
		insInvDtlsEntity1.setIndBuyer("TMI");
		insInvDtlsEntity1.setIndBuyerNm("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		insInvDtlsEntity1.setIndNotifyName("MS. EXPRESS  S.A.  (PTY)  LTD.");
		insInvDtlsEntity1.setIndPayTerm("71");
		insInvDtlsEntity1.setIndScAuthEmp("MR. CHAVALIT VESARNSEATTAPUN");
		insInvDtlsEntity1.setIndProdGrpCd("RHA-Description");
		
		insInvDtlsEntity1.setIndMark1("UMW");
		insInvDtlsEntity1.setIndMark2("722E/812B");
		insInvDtlsEntity1.setIndMark3("SERIES : HILUX");
		insInvDtlsEntity1.setIndMark4("EW0047-0052,EX0084-0089");
		insInvDtlsEntity1.setIndMark5("GA,GB,SA,SB");
		insInvDtlsEntity1.setIndMark7("PORT KLANG, MALAYSIA");
		insInvDtlsEntity1.setIndMark8("Test Mark8");
		insInvDtlsEntity1.setIndGoodsDesc1("COMPONENT PARTS FOR");
		insInvDtlsEntity1.setIndGoodsDesc2("TOYOTA HILUX");
		insInvDtlsEntity1.setIndGoodsDesc3("For Detail See Attachment");
		insInvDtlsEntity1.setIndGoodsDesc4("Goods Desc 4");
		insInvDtlsEntity1.setIndGoodsDesc5("Goods Desc 5");
		insInvDtlsEntity1.setIndGoodsDesc6("Goods Desc 6");
		insInvDtlsEntity1.setIndFreight(99999.99);
		insInvDtlsEntity1.setIndInsurance(99999.99);
		
		Mockito.when(insInvDetailsRepository.save(Mockito.any())).thenReturn(insInvDtlsEntity1);	

		OemCnshMst oemCnshMst = new OemCnshMst();
		oemCnshMst.setId(new OemCnshIdMstEntity("TMI", null, null));
		oemCnshMst.setCmpName("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		Mockito.when(oemCnshMstRepository.findTopByCmpName("PT.TOYOTA MOTOR MANUFACTURING INDONESIA")).thenReturn(oemCnshMst);	
		
		OemCnshMst oemCnshMst1 = new OemCnshMst();
		oemCnshMst1.setId(new OemCnshIdMstEntity("TMI", null, null));
		oemCnshMst1.setCmpName("MS. EXPRESS  S.A.  (PTY)  LTD.");
		Mockito.when(oemCnshMstRepository.findTopByCmpName("MS. EXPRESS  S.A.  (PTY)  LTD.")).thenReturn(oemCnshMst1);	
		
		UpdateInvDetailsRequestDTO updateRequest=new UpdateInvDetailsRequestDTO();
		updateRequest.setInvNo("KR22106826");
		updateRequest.setCustCode("TMC-TOYOTA MOTOR CORPORATION");
		updateRequest.setConsineeName("PT.TOYOTA MOTOR MANUFACTURING INDONESIA");
		updateRequest.setNotifyPartyName("MS. EXPRESS  S.A.  (PTY)  LTD.");
		updateRequest.setPaymentTermObj("71");
		updateRequest.setScAuthorize("MR. CHAVALIT VESARNSEATTAPUN");
		updateRequest.setProductGrpObj("RHA-Description");
		
		updateRequest.setIndMark1("UMW");
		updateRequest.setIndMark2("722E/812B");
		updateRequest.setIndMark3("SERIES : HILUX");
		updateRequest.setIndMark4("EW0047-0052,EX0084-0089");
		updateRequest.setIndMark5("GA,GB,SA,SB");
		updateRequest.setIndMark7("PORT KLANG, MALAYSIA");
		updateRequest.setIndMark8("Test Mark8");
		updateRequest.setIndGoodsDesc1("COMPONENT PARTS FOR");
		updateRequest.setIndGoodsDesc2("TOYOTA HILUX");
		updateRequest.setIndGoodsDesc3("For Detail See Attachment");
		updateRequest.setIndGoodsDesc4("Goods Desc 4");
		updateRequest.setIndGoodsDesc5("Goods Desc 5");
		updateRequest.setIndGoodsDesc6("Goods Desc 6");
		updateRequest.setFreight("99,999.99");
		updateRequest.setInsurance("99,999.98");
		
		Assertions.assertDoesNotThrow(() -> invSearchServiceImpl.updateInvDetailsByInvNo(updateRequest));
	}
}
