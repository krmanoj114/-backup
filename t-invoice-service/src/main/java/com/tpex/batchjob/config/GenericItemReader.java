package com.tpex.batchjob.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpex.util.TpexConfigurationUtil;

@Component
public class GenericItemReader {

	@Autowired
    private GenericLineMapper genericLineMapper;
	
    @Autowired
	TpexConfigurationUtil tpexConfigurationUtil;
	
}
