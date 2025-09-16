package com.tpex.batchjob.partmasterupload;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.PartMasterUploadBatchDto;
import com.tpex.entity.PartMasterEntity;
import com.tpex.util.DateUtil;

public class PartMasterUploadProcessor implements ItemProcessor<PartMasterUploadBatchDto, PartMasterEntity>{

	@Value("#{jobParameters['companyCode']}")
	private String companyCode;

	@Value("#{jobParameters['userId']}")
	private String userId;

	@Override
	public PartMasterEntity process(PartMasterUploadBatchDto item) throws Exception {

		final PartMasterEntity partMasterEntity = new PartMasterEntity();

		partMasterEntity.setPartNo(item.getPartNo());
		partMasterEntity.setPartName(item.getPartName());
		partMasterEntity.setType(item.getPartType());
		partMasterEntity.setInhouseShop(item.getInhouseShop());
		partMasterEntity.setWeight(item.getPartWeight());
		partMasterEntity.setUpdateBy(userId);
		partMasterEntity.setUpdateDate(DateUtil.convertToSqlDate(LocalDate.now()).toString());
		partMasterEntity.setBatchUpdateDate(DateUtil.convertToSqlDate(LocalDate.now()).toString());
		partMasterEntity.setCmpCode(companyCode);

		return partMasterEntity;
	}

}
