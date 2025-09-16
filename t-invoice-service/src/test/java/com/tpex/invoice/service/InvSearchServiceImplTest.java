package com.tpex.invoice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import com.tpex.invoice.serviceimpl.InvSearchServiceImpl;
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

	private static final String IND_CUST_NAME = "TOYOTA MOTOR CORPORATION";

	private static final String IND_GOODS_DESC_6 = "Goods Desc 6";

	private static final String IND_GOODS_DESC_5 = "Goods Desc 5";

	private static final String IND_GOODS_DESC_4 = "Goods Desc 4";

	private static final String IND_GOODS_DESC_3 = "For Detail See Attachment";

	private static final String IND_GOODS_DESC_2 = "TOYOTA HILUX";

	private static final String IND_GOODS_DESC_1 = "COMPONENT PARTS FOR";

	private static final String IND_MARK_8 = "Test Mark8";

	private static final String IND_MARK_7 = "PORT KLANG, MALAYSIA";

	private static final String IND_MARK_5 = "GA,GB,SA,SB";

	private static final String IND_MARK_4 = "EW0047-0052,EX0084-0089";

	private static final String IND_MARK_3 = "SERIES : HILUX";

	private static final String IND_MARK_2 = "722E/812B";

	private static final String IND_PROD_GRP_CODE = "RHA-Description";

	private static final String IND_SC_AUTH_EMP = "MR. CHAVALIT VESARNSEATTAPUN";

	private static final String IND_NOTIFY_NAME = "MS. EXPRESS  S.A.  (PTY)  LTD.";

	private static final String IND_BUYER_NAME = "PT.TOYOTA MOTOR MANUFACTURING INDONESIA";

	private static final String INDINVOICE = "KR22106826";

	private static final String NAME = "LAEM CHABANG";

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
	
	/*@Test
	void testfetchHaisenDetail() throws Exception {
		List<Object[]> response = new ArrayList<>();	
		
		List<OemPortMstEntity> emPortMstObj = new ArrayList<>();
		
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
				
		OemPortMstEntity oemPortMstEntity1=new OemPortMstEntity();
		oemPortMstEntity1.setCd("LCH");
		oemPortMstEntity1.setName(NAME);
		
		OemPortMstEntity oemPortMstEntity2=new OemPortMstEntity();
		oemPortMstEntity2.setCd("LCH");
		oemPortMstEntity2.setName(NAME);
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
	}*/
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
		oemPortMstEntity.setName(NAME);
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

		
	@Test
	void updateInvDetailsByInvNoTest() {
		InsInvDtlsEntity insInvDtlsEntity = new InsInvDtlsEntity();
		insInvDtlsEntity.setIndInvNo(INDINVOICE);
		insInvDtlsEntity.setIndCust("TMC");
		insInvDtlsEntity.setIndCustNm(IND_CUST_NAME);
		insInvDtlsEntity.setIndBuyer("TMI");
		insInvDtlsEntity.setIndBuyerNm(IND_BUYER_NAME);
		insInvDtlsEntity.setIndNotifyName(IND_NOTIFY_NAME);
		insInvDtlsEntity.setIndPayTerm("71");
		insInvDtlsEntity.setIndScAuthEmp(IND_SC_AUTH_EMP);
		insInvDtlsEntity.setIndProdGrpCd(IND_PROD_GRP_CODE);
		
		insInvDtlsEntity.setIndMark1("UMW");
		insInvDtlsEntity.setIndMark2(IND_MARK_2);
		insInvDtlsEntity.setIndMark3(IND_MARK_3);
		insInvDtlsEntity.setIndMark4(IND_MARK_4);
		insInvDtlsEntity.setIndMark5(IND_MARK_5);
		insInvDtlsEntity.setIndMark7(IND_MARK_7);
		insInvDtlsEntity.setIndMark8(IND_MARK_8);
		insInvDtlsEntity.setIndGoodsDesc1(IND_GOODS_DESC_1);
		insInvDtlsEntity.setIndGoodsDesc2(IND_GOODS_DESC_2);
		insInvDtlsEntity.setIndGoodsDesc3(IND_GOODS_DESC_3);
		insInvDtlsEntity.setIndGoodsDesc4(IND_GOODS_DESC_4);
		insInvDtlsEntity.setIndGoodsDesc5(IND_GOODS_DESC_5);
		insInvDtlsEntity.setIndGoodsDesc6(IND_GOODS_DESC_6);
		insInvDtlsEntity.setIndFreight(0.00);
		insInvDtlsEntity.setIndInsurance(0.00);
		
		Mockito.when(insInvDetailsRepository.findById(Mockito.anyString())).thenReturn(Optional.of(insInvDtlsEntity));	

		InsInvDtlsEntity insInvDtlsEntity1 = new InsInvDtlsEntity();
		insInvDtlsEntity1.setIndInvNo(INDINVOICE);
		insInvDtlsEntity1.setIndCust("TMC");
		insInvDtlsEntity1.setIndCustNm(IND_CUST_NAME);
		insInvDtlsEntity1.setIndBuyer("TMI");
		insInvDtlsEntity1.setIndBuyerNm(IND_BUYER_NAME);
		insInvDtlsEntity1.setIndNotifyName(IND_NOTIFY_NAME);
		insInvDtlsEntity1.setIndPayTerm("71");
		insInvDtlsEntity1.setIndScAuthEmp(IND_SC_AUTH_EMP);
		insInvDtlsEntity1.setIndProdGrpCd(IND_PROD_GRP_CODE);
		
		insInvDtlsEntity1.setIndMark1("UMW");
		insInvDtlsEntity1.setIndMark2(IND_MARK_2);
		insInvDtlsEntity1.setIndMark3(IND_MARK_3);
		insInvDtlsEntity1.setIndMark4(IND_MARK_4);
		insInvDtlsEntity1.setIndMark5(IND_MARK_5);
		insInvDtlsEntity1.setIndMark7(IND_MARK_7);
		insInvDtlsEntity1.setIndMark8(IND_MARK_8);
		insInvDtlsEntity1.setIndGoodsDesc1(IND_GOODS_DESC_1);
		insInvDtlsEntity1.setIndGoodsDesc2(IND_GOODS_DESC_2);
		insInvDtlsEntity1.setIndGoodsDesc3(IND_GOODS_DESC_3);
		insInvDtlsEntity1.setIndGoodsDesc4(IND_GOODS_DESC_4);
		insInvDtlsEntity1.setIndGoodsDesc5(IND_GOODS_DESC_5);
		insInvDtlsEntity1.setIndGoodsDesc6(IND_GOODS_DESC_6);
		insInvDtlsEntity1.setIndFreight(99999.99);
		insInvDtlsEntity1.setIndInsurance(99999.99);
		
		Mockito.when(insInvDetailsRepository.save(Mockito.any())).thenReturn(insInvDtlsEntity1);	

		OemCnshMst oemCnshMst = new OemCnshMst();
		oemCnshMst.setId(new OemCnshIdMstEntity("TMI", null, null));
		oemCnshMst.setCmpName(IND_BUYER_NAME);
		Mockito.when(oemCnshMstRepository.findTopByCmpName(IND_BUYER_NAME)).thenReturn(oemCnshMst);	
		
		OemCnshMst oemCnshMst1 = new OemCnshMst();
		oemCnshMst1.setId(new OemCnshIdMstEntity("TMI", null, null));
		oemCnshMst1.setCmpName(IND_NOTIFY_NAME);
		Mockito.when(oemCnshMstRepository.findTopByCmpName(IND_NOTIFY_NAME)).thenReturn(oemCnshMst1);	
		
		UpdateInvDetailsRequestDTO updateRequest=new UpdateInvDetailsRequestDTO();
		updateRequest.setInvNo(INDINVOICE);
		updateRequest.setCustCode("TMC-TOYOTA MOTOR CORPORATION");
		updateRequest.setConsineeName(IND_BUYER_NAME);
		updateRequest.setNotifyPartyName(IND_NOTIFY_NAME);
		updateRequest.setPaymentTermObj("71");
		updateRequest.setScAuthorize(IND_SC_AUTH_EMP);
		updateRequest.setProductGrpObj(IND_PROD_GRP_CODE);
		
		updateRequest.setIndMark1("UMW");
		updateRequest.setIndMark2(IND_MARK_2);
		updateRequest.setIndMark3(IND_MARK_3);
		updateRequest.setIndMark4(IND_MARK_4);
		updateRequest.setIndMark5(IND_MARK_5);
		updateRequest.setIndMark7(IND_MARK_7);
		updateRequest.setIndMark8(IND_MARK_8);
		updateRequest.setIndGoodsDesc1(IND_GOODS_DESC_1);
		updateRequest.setIndGoodsDesc2(IND_GOODS_DESC_2);
		updateRequest.setIndGoodsDesc3(IND_GOODS_DESC_3);
		updateRequest.setIndGoodsDesc4(IND_GOODS_DESC_4);
		updateRequest.setIndGoodsDesc5(IND_GOODS_DESC_5);
		updateRequest.setIndGoodsDesc6(IND_GOODS_DESC_6);
		updateRequest.setFreight("99,999.99");
		updateRequest.setInsurance("99,999.98");
		
		Assertions.assertDoesNotThrow(() -> invSearchServiceImpl.updateInvDetailsByInvNo(updateRequest));
	}
}
