package com.tpex.admin.service;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tpex.admin.module.OndemandDownRptModule;
import com.tpex.admin.repository.RddDownLocDtlRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.stereotype.Service;

import com.tpex.admin.dto.ReportStatusInformation;
import com.tpex.admin.entity.RddDownLocDtlEntity;


/**
 * The Class OndemandDownRptServiceImpl.
 */
@Service
public class OndemandDownRptServiceImpl implements OndemandDownRptService {

	@Autowired
	RddDownLocDtlRepository ondemandDownRptRepository;

	/**
	 * Search on demand reports.
	 *
	 * @param request the request
	 * @return the list
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws ParseException            the parse exception
	 * @throws ParseException            the parse exception
	 */
	@Override
	public List<RddDownLocDtlEntity> searchOnDemandReports(OndemandDownRptModule request)
			throws IllegalAccessException, InvocationTargetException, ParseException, java.text.ParseException {
		List<RddDownLocDtlEntity> list = null;

		SimpleDateFormat dmyFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new SimpleDateFormat("dd/MM/yyyy").parse(request.getCreateDate());
		String formattedDateStr = dmyFormat.format(date);

		if (StringUtils.isNotEmpty(request.getReportName()) && StringUtils.isNotEmpty(request.getStatus())
				&& StringUtils.isNotEmpty(request.getCreateBy()) && StringUtils.isNotEmpty(request.getCreateDate())) {
			list = ondemandDownRptRepository.findByStatusAndCreateDateAndReportNameAndCreateBy(request.getStatus(),
					formattedDateStr, request.getReportName(), request.getCreateBy());
		} else if ((StringUtils.isNotEmpty(request.getReportName()) || StringUtils.isNotEmpty(request.getStatus()))
				&& StringUtils.isNotEmpty(request.getCreateBy()) && StringUtils.isNotEmpty(request.getCreateDate())) {
			list = ondemandDownRptRepository.findByStatusOrReportNameAndCreateByAndCreateDate(request.getStatus(),
					formattedDateStr, request.getReportName(), request.getCreateBy());
		} else if (StringUtils.isNotEmpty(request.getCreateDate()) && StringUtils.isNotEmpty(request.getCreateBy())) {
			list = ondemandDownRptRepository.findByCreateDateAndCreateBy(formattedDateStr, request.getCreateBy());
		}
		return list;
	}

	/**
	 * Fetch report and status.
	 *
	 * @param reportStatusInformation the report status information
	 * @return the report status information
	 */
	@Override
	public ReportStatusInformation fetchReportAndStatus(ReportStatusInformation reportStatusInformation) {
		List<String> listOfReportName = ondemandDownRptRepository.getReportName();
		List<String> listOfStatus = ondemandDownRptRepository.getStatus();
		reportStatusInformation.setReportNames(listOfReportName);
		reportStatusInformation.setStatus(listOfStatus);
		return reportStatusInformation;
	}
}
