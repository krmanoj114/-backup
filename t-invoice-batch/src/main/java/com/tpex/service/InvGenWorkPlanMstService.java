package com.tpex.service;

import com.tpex.dto.UploadWrkPlanMasterFromVesselBookingRequest;
import com.tpex.entity.OemProcessCtrlEntity;

public interface InvGenWorkPlanMstService {


	void wrkPlanMasterUploadBatchJob(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity,
			String effectiveDate, String userId);
	
	public void wrkPlanMasterUploadFromVesselBookingBatchJob(UploadWrkPlanMasterFromVesselBookingRequest request);
	
}
