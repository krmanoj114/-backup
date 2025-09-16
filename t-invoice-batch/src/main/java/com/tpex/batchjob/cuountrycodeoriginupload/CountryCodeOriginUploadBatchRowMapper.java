package com.tpex.batchjob.cuountrycodeoriginupload;



import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.CountryCodeOriginUploadBatchInputDto;

public class CountryCodeOriginUploadBatchRowMapper implements RowMapper<CountryCodeOriginUploadBatchInputDto> {

	@Override
	public CountryCodeOriginUploadBatchInputDto mapRow(RowSet rowSet) throws Exception {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}

		CountryCodeOriginUploadBatchInputDto batchInputDto = new CountryCodeOriginUploadBatchInputDto();

		batchInputDto.setPartNo(rowSet.getCurrentRow()[0]);
		batchInputDto.setPartName(rowSet.getCurrentRow()[1]);
		batchInputDto.setCountryOfOriginCode(rowSet.getCurrentRow()[2]);
		batchInputDto.setVanDateFrom(rowSet.getCurrentRow()[3]);
		batchInputDto.setVanDateTo(rowSet.getCurrentRow()[4]);

		return batchInputDto;
	}

}
