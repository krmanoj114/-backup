package com.tpex.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.admin.entity.BuyerMasterEntity;
import com.tpex.admin.entity.CurrencyMasterEntity;
import com.tpex.admin.dto.ColumnsDTO;
import com.tpex.admin.dto.CoulmnsInfo;
import com.tpex.admin.repository.BuyerMasterRepository;
import com.tpex.admin.repository.CurrencyMasterRepository;

@ExtendWith(MockitoExtension.class)
class CommonCodeMasterServiceImplTest {

	private static final String TMT = "TMT";
	private static final String CMP_CD = "TMT";

	@InjectMocks
	CommonCodeMasterServiceImpl commonCodeMasterService;

	@Mock
	EntityManager entityManager;

	@Mock
	Query query;

	@Mock
	BuyerMasterRepository buyerMasterRepository;

	@Mock
	CurrencyMasterRepository currencyMasterRepository;
	

	@Test
	void codeMasterDataTest() {
		CoulmnsInfo col = new CoulmnsInfo();
		col.setId("test");
		List<CoulmnsInfo> columns = new ArrayList<>();
		ColumnsDTO input = new ColumnsDTO();
		input.setColumns(columns);
		Query q = Mockito.mock(Query.class);

		when(entityManager.createNativeQuery(anyString())).thenReturn(q);

		commonCodeMasterService.codeMasterData(input,CMP_CD);

		verify(q, times(1)).getResultList();
	}

	@Test
	void testExecute() {
		Mockito.when(entityManager.createNativeQuery(anyString())).thenReturn(query);
		Mockito.when(query.getResultList()).thenReturn(anyList());
		commonCodeMasterService.execute("columnName", "tabelName", "orderBy",CMP_CD);
		assertNotNull("Test");
	}
	
	@Test
	void finalDestinationTest() throws Exception {

		List<BuyerMasterEntity> buyerMasterEntityList = new ArrayList<>();
		buyerMasterEntityList.add(new BuyerMasterEntity("002", "CAMBODIA", "PCHEENIM", "2018-12-05", TMT));
		buyerMasterEntityList.add(new BuyerMasterEntity("001", "HONGKONG", "PCHEENIM", "2013-11-05", TMT));
		when(buyerMasterRepository.findAllBycmpCdOrderByBuyerCodeAsc(TMT)).thenReturn(buyerMasterEntityList);

		List<CurrencyMasterEntity> currencyMasterEntityList = new ArrayList<>();
		currencyMasterEntityList.add(new CurrencyMasterEntity("BHT", "THAI", "BHT", "OEM", "2014-11-07", TMT));
		currencyMasterEntityList.add(new CurrencyMasterEntity("INR", "RUPPES", "INR", "NIITTMT", "2014-11-05", TMT));
		when(currencyMasterRepository.findAllBycmpCdOrderByCurrencyCodeAsc(TMT))
		.thenReturn(currencyMasterEntityList);

		commonCodeMasterService.finalDestinationDtls(TMT);
		assertNotNull(buyerMasterEntityList.get(0).getCmpCd());
		assertThat(currencyMasterEntityList.get(0).getCmpCd()).isEqualTo(TMT);

	}
}
