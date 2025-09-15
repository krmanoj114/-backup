package com.tpex.invoice.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.tpex.util.ConstantUtils;

import lombok.Data;

@Data
public class InvoiceNumberRequestDTO {

	@NotNull(message = ConstantUtils.ERR_CM_3001)
	@NotEmpty(message = ConstantUtils.ERR_CM_3001)
	private String invoiceNumber;
}
