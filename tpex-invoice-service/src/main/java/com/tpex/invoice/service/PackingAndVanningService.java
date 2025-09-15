package com.tpex.invoice.service;

import java.io.IOException;

import com.tpex.invoice.dto.CustomLabelResponseDTO;
import com.tpex.invoice.dto.PackingAndVanningResponseDTO;
import com.tpex.invoice.dto.PackingVanningReportResponseDTO;
import com.tpex.invoice.dto.SsLineGroupBoxResponseDTO;
import com.tpex.invoice.dto.VanningPlantResponseDTO;

public interface PackingAndVanningService {

	PackingAndVanningResponseDTO getPackingAndVanningResponseData(String companyCode);

	VanningPlantResponseDTO getVanningPlantResponseData(String companyCode, String planningFlag);

	CustomLabelResponseDTO getCustomLabelResponseData(String companyCode);

	SsLineGroupBoxResponseDTO getSsLineGroupBoxResponseData();

	PackingVanningReportResponseDTO getPackingVanningReport() throws IOException;

}
