package com.tpex.service;

import com.tpex.entity.OemProcessCtrlEntity;

public interface CountryCodeOriginService {
	
	
	void countryCodeOriginBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			 String userId);

}
