package com.tpex.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OemShippingCtrlMstDeleteRequestDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String cfcCode;
	private String impCode;
	private String expCode;
	private String setPartCode;
	private String series;
	private String portOfDischarge;

}
