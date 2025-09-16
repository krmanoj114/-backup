package com.tpex.admin.dto;

import java.io.Serializable;

import com.tpex.admin.entity.OemProcessCtrlEntity;

import lombok.Data;

@Data
public class SendSeparateInvoiceToIXOSDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userId;
	private String batchName;
	private String fileName;
	private String companyCode;
	private String invoiceNo;
	private String haisenNo;
	private String haisenYearMonth;
	private OemProcessCtrlEntity oemProcessCtrlEntity;
	private String vanMonth;
	private String systemName;
	private String batchId;	

}
