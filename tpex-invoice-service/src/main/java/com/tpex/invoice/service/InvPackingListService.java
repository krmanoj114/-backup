package com.tpex.invoice.service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;

public interface InvPackingListService {

	Object getInvPackingListRptDownload(String invNumber, String reportName, String reportFormat) throws Exception;

}
