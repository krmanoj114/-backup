package com.tpex.service;

import com.tpex.entity.OemProcessCtrlEntity;

/**
 * The Interface AddressMasterService.
 */
public interface AddressMasterService {

	/**
	 * Address master upload batch job.
	 *
	 * @param batchName the batch name
	 * @param fileName the file name
	 * @param oemProcessCtrlEntity the oem process ctrl entity
	 * @param companyCode the company code
	 * @param userId the user id
	 */
	void addressMasterUploadBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String companyCode, String userId);

}