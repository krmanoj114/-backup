package com.tpex.month.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.month.model.entity.TpexConfigEntity;
import com.tpex.month.model.repository.TpexConfigRepository;

@ExtendWith(MockitoExtension.class)
class TpexConfigUtilsTest {

	@InjectMocks
	private TpexConfigUtils tpexConfigUtils;
	
	@Mock
	private TpexConfigRepository tpexConfigRepository;

	@BeforeEach
	void init() {
		List<TpexConfigEntity> configList = new ArrayList<>();
		TpexConfigEntity config = new TpexConfigEntity();
		config.setId(1);
		config.setName("CodeCoveragePercent");
		config.setValue("80");
		configList.add(config);
		config = new TpexConfigEntity();
		config.setId(2);
		config.setName("OtherConfig");
		config.setValue("ABC");
		configList.add(config);
		
		when(tpexConfigRepository.findAll()).thenReturn(configList);
		tpexConfigUtils.init();
	}
	
	@Test
	void getConfigValueTest() {
		String cc = tpexConfigUtils.getConfigValue("CodeCoveragePercent");
		String other = tpexConfigUtils.getConfigValue("XXX");

		assertEquals("80", cc);
		assertNull(other);
	}
}
