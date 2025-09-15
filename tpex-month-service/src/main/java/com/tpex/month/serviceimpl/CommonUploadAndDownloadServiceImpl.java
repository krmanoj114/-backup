package com.tpex.month.serviceimpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.month.model.entity.OemProcessCtrlEntity;
import com.tpex.month.model.entity.OemProcessCtrlIdEntity;
import com.tpex.month.model.repository.OemProcessCtrlRepository;
import com.tpex.month.service.CommonUploadAndDownloadService;
import com.tpex.month.util.ConstantUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommonUploadAndDownloadServiceImpl implements CommonUploadAndDownloadService {

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Override
	public OemProcessCtrlEntity saveProcessDetails(String userId, String batchName) {

		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		int idcount = oemProcessCtrlRepository.getIdOfProcessControl();
		id.setBatchId(batchName);
		id.setProcessControlId(idcount);
		entity.setId(id);
		entity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setProgramId(batchName);
		entity.setStatus(2);
		entity.setUserId(userId);
		entity.setDeamon("N");
		entity.setProgramName(ConstantUtil.VESSEL_BOOKING_MASTER_UPLOAD);
		entity.setSystemName(ConstantUtil.TPEX);
		entity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		oemProcessCtrlRepository.save(oemProcessCtrlRepository.save(entity));
		return entity;
	}
}
