package com.tpex.service;

import com.tpex.dto.UploadPxpPriceMasterJobDto;

/**
 * The Interface PxpPriceMasterService.
 */
public interface PxpPriceMasterService {
	
	/**
	 * Pxp price master upload batch job.
	 *
	 * @param uploadPxpPriceMasterJobDto the upload pxp price master job dto
	 */
	void pxpPriceMasterUploadBatchJob(UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto);

}