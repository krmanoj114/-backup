package com.tpex.invoice.service;

import com.tpex.invoice.dto.SeprateInvGenResponseWrapper;

public interface SeperateInvGenService {

	SeprateInvGenResponseWrapper searchByInvNo(String invoiceNo,String carFamily,String partNo);
}
