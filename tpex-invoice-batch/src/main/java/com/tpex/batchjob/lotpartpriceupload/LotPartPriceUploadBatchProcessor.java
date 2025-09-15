package com.tpex.batchjob.lotpartpriceupload;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.LotPartPriceUploadBatchDto;
import com.tpex.dto.LotPartPriceUploadBatchInputDto;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

public class LotPartPriceUploadBatchProcessor implements ItemProcessor<LotPartPriceUploadBatchInputDto, LotPartPriceUploadBatchDto> {

	@Value("#{jobParameters['effectiveDate']}")
	private String effectiveDate;
	
	@Value("#{jobParameters['userId']}")
	private String userId;
	
	@Override
	public LotPartPriceUploadBatchDto process(LotPartPriceUploadBatchInputDto item) throws Exception {
		final LotPartPriceUploadBatchDto transformedLotPartPrice = new LotPartPriceUploadBatchDto();
		
		transformedLotPartPrice.setPartPricericeCFCode(item.getCfcCode());
		transformedLotPartPrice.setPartPriceDestCode(item.getImpCode());
		transformedLotPartPrice.setPartPriceLotCode(item.getLot());
		transformedLotPartPrice.setPartPriceCurrCode(item.getCurrencyCode());
		transformedLotPartPrice.setPartPriceNo(item.getPartNo());
		transformedLotPartPrice.setPartPricePrc(Double.parseDouble(item.getFirstOfPrice()));
		transformedLotPartPrice.setPartPriceUpdateBy(userId == null ? ConstantUtils.TEST_USER : userId);
		transformedLotPartPrice.setPartPriceUpdateDate(DateUtil.convertToSqlDate(LocalDate.now()).toString());
		transformedLotPartPrice.setPartPriceName(item.getPartName());
		transformedLotPartPrice.setPartPriceEffFromMonth(effectiveDate);
		transformedLotPartPrice.setPartPriceEffToMonth(effectiveDate);
		transformedLotPartPrice.setPartPriceUsage(Double.parseDouble(item.getUsage()));
		transformedLotPartPrice.setCompanyCode(null);
		
		transformedLotPartPrice.setPriceCFCode(item.getCfcCode());
		transformedLotPartPrice.setPriceDestCode(item.getImpCode());
		transformedLotPartPrice.setPriceLotCode(item.getLot());
		transformedLotPartPrice.setPriceCurrCode(item.getCurrencyCode());
		transformedLotPartPrice.setLotPrice(null);
		transformedLotPartPrice.setUpdateBy(userId == null ? ConstantUtils.TEST_USER : userId);
		transformedLotPartPrice.setUpdateDate(DateUtil.convertToSqlDate(LocalDate.now()));
		transformedLotPartPrice.setEffFromMonth(effectiveDate);
		transformedLotPartPrice.setEffFromToMonth(effectiveDate);
		transformedLotPartPrice.setLotPriceStatus(null);
		
        return transformedLotPartPrice;
	}

}
