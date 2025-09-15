package com.tpex.invoice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingContSearchInputDTO {

	private String etdFrom;
	private String etdTo;
	private String containerDestination;
	private String bookingNo;
	private List<String> renbanCodes;
}
