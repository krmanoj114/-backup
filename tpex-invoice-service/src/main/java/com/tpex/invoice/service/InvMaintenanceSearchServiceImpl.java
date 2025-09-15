package com.tpex.invoice.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.dto.InvoiceMaintenanceDTO;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.repository.InvoiceMaintenanceRepository;
import com.tpex.util.ConstantUtils;

/**
 * The Class InvMaintenanceSearchServiceImpl.
 */
@Service
public class InvMaintenanceSearchServiceImpl implements InvMaintenanceSearchService {

	@Autowired
	private InvoiceMaintenanceRepository invoiceMaintenanceRepository;

	/**
	 * Fetch order type and invoice number.
	 *
	 * @return the list
	 */
	@Override
	public List<InvoiceMaintenanceDTO> fetchOrderTypeAndInvoiceNumber() {
		List<InsInvDtlsEntity> listOfInvoiceEntity = invoiceMaintenanceRepository.findByIndCancelFlagEqualsAndIndPlsSndDtNotNullOrderByIndInvNo(ConstantUtils.FLAG);
		return listOfInvoiceEntity.stream()
				.map(e -> new InvoiceMaintenanceDTO(e.getIndOrdTyp(), e.getIndInvNo()+ "-" + e.getIndFinalDst())).collect(Collectors.toList());
	}

}
