package com.tpex.batchjob.cuountrycodeoriginupload;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.CountryCodeOriginUploadBatchInputDto;
import com.tpex.entity.CountryOriginMstEntity;
import com.tpex.repository.CountryOriginMstRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

public class CountryCodeOriginUploadBatchProcessor implements ItemProcessor<CountryCodeOriginUploadBatchInputDto, CountryOriginMstEntity>{

	@Value("#{jobParameters['userId']}")
	private String userId;

	public static final String DATE_FORMAT = ConstantUtils.DEFAULT_DATE_FORMATE;

	@Autowired
	CountryOriginMstRepository countryOriginMstRepository;

	public CountryOriginMstEntity process(CountryCodeOriginUploadBatchInputDto batchInputDto) throws Exception {

		final CountryOriginMstEntity entity = new CountryOriginMstEntity();

		entity.setPartName(batchInputDto.getPartName());
		entity.setPartNumber(batchInputDto.getPartNo());
		entity.setCountryOrigin(batchInputDto.getCountryOfOriginCode().split("-")[0]);
		entity.setEffectiveFromdate(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,batchInputDto.getVanDateFrom()));
		entity.setEffectivetodate(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,batchInputDto.getVanDateTo()));
		entity.setCreatedby(userId);
		entity.setUpdatedBy(userId);
		entity.setCreatedDate(Date.valueOf(LocalDate.now()));
		entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
		return entity;
	}
}
