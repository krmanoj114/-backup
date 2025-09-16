package com.tpex.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tpex.entity.TpexConfigEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRParameter;

@Component
public class TpexConfigurationUtil {

	@Autowired
	TpexConfigRepository  tpexConfigRepository;

	public String getFilePath(String filename) {
		String filePath = "";
		TpexConfigEntity tpexConfigEntity = tpexConfigRepository.findByName(filename);
		filePath = tpexConfigEntity.getValue();
		if(StringUtils.isBlank(filePath)) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put("filename", filename);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_CM_3017, errorMessageParams); 
		}
		return filePath;
	}
	
	public Map<String, Object> getReportDynamicPrameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("headingFontSize", Integer.parseInt(tpexConfigRepository.findByName("jasper.report.headingFontSize").getValue()));
		parameters.put("detailFontSize", Integer.parseInt(tpexConfigRepository.findByName("jasper.report.detailFontSize").getValue()));
		parameters.put("headingFontColor", tpexConfigRepository.findByName("jasper.report.headingFontColor").getValue());
		parameters.put("detailFontColor", tpexConfigRepository.findByName("jasper.report.detailFontColor").getValue());
		parameters.put("headingBGColor", tpexConfigRepository.findByName("jasper.report.headingBGColor").getValue());
		parameters.put("detailBGColor", tpexConfigRepository.findByName("jasper.report.detailBGColor").getValue());
		parameters.put("detailVAlign", tpexConfigRepository.findByName("jasper.report.detailVAlign").getValue());
		return parameters;
	}
	
}
