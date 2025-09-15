package com.tpex.batchjob.carfamilydestination;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.CarFamilyDstMasterUploadBatchDto;

public class CarFamilyDestMasterUploadRowMapper implements RowMapper<CarFamilyDstMasterUploadBatchDto> {

	@Override
	public CarFamilyDstMasterUploadBatchDto mapRow(RowSet rowSet) {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}
		
		CarFamilyDstMasterUploadBatchDto carFamilyDstMasterUploadBatchDto = new CarFamilyDstMasterUploadBatchDto();
		carFamilyDstMasterUploadBatchDto.setCarFmlyCode(rowSet.getCurrentRow()[0].trim());
		carFamilyDstMasterUploadBatchDto.setDestinationCode(rowSet.getCurrentRow()[1].trim());
		carFamilyDstMasterUploadBatchDto.setReExporterCode(rowSet.getCurrentRow()[2].trim());
		carFamilyDstMasterUploadBatchDto.setSrsName(rowSet.getCurrentRow()[3].trim());
		return carFamilyDstMasterUploadBatchDto;
	}
}
