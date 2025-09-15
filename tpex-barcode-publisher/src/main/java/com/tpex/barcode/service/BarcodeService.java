package com.tpex.barcode.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.barcode.entity.TpexBarcodeEntity;
import com.tpex.barcode.repository.TpexBarcodeRepository;
@Service
public class BarcodeService {

	@Autowired
	TpexBarcodeRepository barcodeRepository;
	
	public TpexBarcodeEntity saveBarcodeData(TpexBarcodeEntity gun) {
        return barcodeRepository.save(gun);
    }
}
