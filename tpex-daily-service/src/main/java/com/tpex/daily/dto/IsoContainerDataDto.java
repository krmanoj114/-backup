package com.tpex.daily.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IsoContainerDataDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String etd;

	private String bookingNo;

	private String containerRanbanNo;

	private String isoContainerNo;

	private String containerType;

	private String sealNo;

	private String tareWeight;

	private String containerSize;

	private String shipComp;

	private String vanningStatus;

}
