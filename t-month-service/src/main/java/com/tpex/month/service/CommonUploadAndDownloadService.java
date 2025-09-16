package com.tpex.month.service;

import com.tpex.month.model.entity.OemProcessCtrlEntity;

public interface CommonUploadAndDownloadService {

	OemProcessCtrlEntity saveProcessDetails(String userId, String batchParameter);
}
