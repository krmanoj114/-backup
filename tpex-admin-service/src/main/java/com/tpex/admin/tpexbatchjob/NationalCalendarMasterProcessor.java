package com.tpex.admin.tpexbatchjob;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.item.ItemProcessor;

import com.tpex.admin.dto.NatCalDTO;
import com.tpex.admin.entity.NatCalEntity;
import com.tpex.admin.entity.NatCalIdEntity;
import com.tpex.admin.util.Util;


/**
 * The Class NationalCalendarMasterProcessor.
 */
public class NationalCalendarMasterProcessor implements ItemProcessor<NatCalDTO, NatCalEntity> {
	
	// This method transforms data from one form to another.
	
	/**
	 * Process.
	 *
	 * @param dto the dto
	 * @return the nat cal entity
	 * @throws Exception the exception
	 */
	@Override
	public NatCalEntity process(NatCalDTO dto) throws Exception {
		
		NatCalEntity natCalEntity= new NatCalEntity();
		NatCalIdEntity idEntity = new NatCalIdEntity();
		idEntity.setYear(dto.getYear());
		idEntity.setMonth(dto.getMonth());
		idEntity.setDay(dto.getDay());
		natCalEntity.setId(idEntity);
		
		if(Util.nullCheck(dto.getWhd()) && ("W".equalsIgnoreCase(dto.getWhd()) || "H".equalsIgnoreCase(dto.getWhd())))
		{
			natCalEntity.setWhd(dto.getWhd());
		}else {
			natCalEntity.setWhd("W");
		}
		
		natCalEntity.setCrtDt(new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));
		natCalEntity.setCrtBy(dto.getCrtBy());
		natCalEntity.setUpdBy(dto.getUpdBy());
		natCalEntity.setUpdDt(new Timestamp(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH).getTime()));
		natCalEntity.setRemarks(dto.getRemarks());
				
		return natCalEntity;
	}
}
