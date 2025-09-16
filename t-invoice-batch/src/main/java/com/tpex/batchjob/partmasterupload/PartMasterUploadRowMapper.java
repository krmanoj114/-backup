package com.tpex.batchjob.partmasterupload;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.PartMasterUploadBatchDto;

public class PartMasterUploadRowMapper implements RowMapper<PartMasterUploadBatchDto>{

	@Override
	public PartMasterUploadBatchDto mapRow(RowSet rowSet) {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}

		PartMasterUploadBatchDto partMasterUploadBatchDto = new PartMasterUploadBatchDto();
		partMasterUploadBatchDto.setPartNo(rowSet.getCurrentRow()[0].trim());
		partMasterUploadBatchDto.setPartName(rowSet.getCurrentRow()[1].trim());
		partMasterUploadBatchDto.setPartType(rowSet.getCurrentRow()[2].trim());
		partMasterUploadBatchDto.setInhouseShop(rowSet.getCurrentRow()[3].trim());
		partMasterUploadBatchDto.setPartWeight(rowSet.getCurrentRow()[4].trim());

		return partMasterUploadBatchDto;
	}

}
