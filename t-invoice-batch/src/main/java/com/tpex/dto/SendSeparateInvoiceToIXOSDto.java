package com.tpex.dto;

import java.io.Serializable;

import com.tpex.entity.OemProcessCtrlEntity;

import lombok.Data;

@Data
public class SendSeparateInvoiceToIXOSDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String systemName;
	private String batchId;
	private String fileName;
	private String companyCode;
	private String invoiceNo;
	private String haisenNo;
	private String haisenYearMonth;
	private OemProcessCtrlEntity oemProcessCtrlEntity;

}
