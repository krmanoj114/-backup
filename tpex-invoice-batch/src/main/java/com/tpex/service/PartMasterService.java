package com.tpex.service;

import com.tpex.entity.OemProcessCtrlEntity;

public interface PartMasterService {
	
	void partMasterUploadJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String companyCode, String userId);
}
