package com.tpex.service;

import com.tpex.entity.OemProcessCtrlEntity;

/**
 * The Interface CarFamilyDstMasterService.
 */
public interface CarFamilyDstMasterService {
	
	/**
	 * Car family dst master upload batch job.
	 *
	 * @param batchName the batch name
	 * @param fileName the file name
	 * @param oemProcessCtrlEntity the oem process ctrl entity
	 * @param companyCode the company code
	 * @param userId the user id
	 */
	void carFamilyDstMasterUploadBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String companyCode, String userId);

}