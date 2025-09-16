package com.tpex.admin.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.tpex.admin.module.OndemandDownRptModule;
import org.springframework.expression.ParseException;

import com.tpex.admin.dto.ReportStatusInformation;
import com.tpex.admin.entity.RddDownLocDtlEntity;


/**
 * The Interface OndemandDownRptService.
 */
public interface OndemandDownRptService {

	List<RddDownLocDtlEntity> searchOnDemandReports(OndemandDownRptModule request)
			throws IllegalAccessException, InvocationTargetException, ParseException, java.text.ParseException;

	ReportStatusInformation fetchReportAndStatus(ReportStatusInformation reportStatusInformation);

}
