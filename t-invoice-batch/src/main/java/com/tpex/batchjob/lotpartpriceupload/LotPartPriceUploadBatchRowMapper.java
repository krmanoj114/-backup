package com.tpex.batchjob.lotpartpriceupload;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.LotPartPriceUploadBatchInputDto;

public class LotPartPriceUploadBatchRowMapper implements RowMapper<LotPartPriceUploadBatchInputDto> {

	@Override
	public LotPartPriceUploadBatchInputDto mapRow(RowSet rowSet) throws Exception {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}

		LotPartPriceUploadBatchInputDto lotPartPriceUploadBatchInputDto = new LotPartPriceUploadBatchInputDto();
		lotPartPriceUploadBatchInputDto.setCurrencyCode(rowSet.getCurrentRow()[0]);
		lotPartPriceUploadBatchInputDto.setLot(rowSet.getCurrentRow()[1]);
		lotPartPriceUploadBatchInputDto.setCfcCode(rowSet.getCurrentRow()[2]);
		lotPartPriceUploadBatchInputDto.setImpCode(rowSet.getCurrentRow()[3]);
		lotPartPriceUploadBatchInputDto.setPartNo(rowSet.getCurrentRow()[4]);
		lotPartPriceUploadBatchInputDto.setPartName(rowSet.getCurrentRow()[5]);
		lotPartPriceUploadBatchInputDto.setFirstOfPrice(rowSet.getCurrentRow()[6]);
		lotPartPriceUploadBatchInputDto.setUsage(rowSet.getCurrentRow()[7]);
		
		return lotPartPriceUploadBatchInputDto;
	}
}
