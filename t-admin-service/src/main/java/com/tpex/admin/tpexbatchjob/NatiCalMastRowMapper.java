package com.tpex.admin.tpexbatchjob;


import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.admin.dto.NatCalDTO;

/**
 * The Class NatiCalMastRowMapper.
 */
public class NatiCalMastRowMapper implements RowMapper<NatCalDTO> {

	@Override
	public NatCalDTO mapRow(RowSet rowSet) throws Exception {


		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}

		NatCalDTO oemNatCalDTO = new NatCalDTO();

		oemNatCalDTO.setYear(Integer.parseInt(rowSet.getCurrentRow()[0]));
		
		DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("MMM");
		TemporalAccessor temporalAccessor = dtFormatter.parse(rowSet.getCurrentRow()[1]);
		oemNatCalDTO.setMonth(temporalAccessor.get(ChronoField.MONTH_OF_YEAR));
		oemNatCalDTO.setDay(Integer.parseInt(rowSet.getCurrentRow()[2]));
		oemNatCalDTO.setWhd(rowSet.getCurrentRow()[4]);
		oemNatCalDTO.setRemarks(rowSet.getCurrentRow()[5]);


		return  oemNatCalDTO;
	}
}
