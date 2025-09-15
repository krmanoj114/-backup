package com.tpex.invoice.service;

import java.util.List;

import com.tpex.dto.InvoiceMaintenanceDTO;

/**
 * The Interface InvMaintenanceSearchService.
 */
public interface InvMaintenanceSearchService {

	List<InvoiceMaintenanceDTO> fetchOrderTypeAndInvoiceNumber();

}
