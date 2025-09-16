package com.tpex.auth.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.tpex.auth.entity.TbMTpexConfigEntity;
import com.tpex.auth.repository.TbMTpexConfigRepository;

@Component
public class InitialValueUtil {

	//avoid conflict bean
	@Autowired
	@Qualifier("tpexConfigAuth")
	private TbMTpexConfigRepository tpexConfigRepository;
	
	private static Map<String, String> configMap = new HashMap<>();

	@PostConstruct
	public void init() {
		List<TbMTpexConfigEntity> allConfig = tpexConfigRepository.findAll();
		for (TbMTpexConfigEntity config : allConfig) {
			configMap.put(config.getName(), config.getValue());
		}
	}

	public String getConfigValue(String key){
		return configMap.get(key);
	}
}
