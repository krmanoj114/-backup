package com.tpex.service;

import com.tpex.entity.OemProcessCtrlEntity;

/**
 * The Interface LotPriceMasterService.
 */
public interface LotPriceMasterService {

	/**
	 * Lot part price upload batch job.
	 *
	 * @param batchName the batch name
	 * @param fileName the file name
	 * @param oemProcessCtrlEntity the oem process ctrl entity
	 * @param effectiveDate the effective date
	 * @param userId the user id
	 */
	void lotPartPriceUploadBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String effectiveDate, String userId);
	
}