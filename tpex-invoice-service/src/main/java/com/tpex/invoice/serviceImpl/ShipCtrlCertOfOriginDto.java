package com.tpex.invoice.serviceimpl;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipCtrlCertOfOriginDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String certificationOfOriginReport;
	private String certificationOfOriginReportValue;

}
