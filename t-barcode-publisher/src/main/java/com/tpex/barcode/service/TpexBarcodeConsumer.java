package com.tpex.barcode.service;

import org.springframework.stereotype.Service;

@Service
public class TpexBarcodeConsumer {
	//@Autowired
//	TpexBarcodeRepository barcodeRepository;
	//@Autowired
	//TpexBarcodeProducer tpexBarcodeProducer;

	//@JmsListener(destination = "barcode-topic")
	public void receiveMessage(String message) {
		/*
		 * final Logger log = LoggerFactory.getLogger(TpexBarcodeConsumer.class); //
		 * Process and save the JSON data to the database try {
		 * log.info("TpexBarcodeConsumer::receiveMessage: - Receive Message started");
		 * JSONObject jsonObject = new JSONObject(message); String gunId =
		 * jsonObject.getString("gunId"); String scanData =
		 * jsonObject.getString("scanData");
		 * 
		 * 
		 * TpexBarcodeEntity tpexBarcodeEntity = new TpexBarcodeEntity();
		 * tpexBarcodeEntity.setGunId(gunId); tpexBarcodeEntity.setScanData(scanData);
		 * 
		 * log.info("TpexBarcodeConsumer::receiveMessage Details: GunId={}, ScanData={}"
		 * , tpexBarcodeEntity.getGunId(), tpexBarcodeEntity.getScanData());
		 * barcodeRepository.save(tpexBarcodeEntity); String status = gunId + ":" +
		 * "OK"; tpexBarcodeProducer.sendStatusToTopic(status);
		 * log.info("TpexBarcodeConsumer::Status return to topic: status={}", status);
		 * 
		 * log.info("TpexBarcodeConsumer::receiveMessage: - Receive Message End"); }
		 * catch (Exception e) { log.error(e.getMessage());
		 * 
		 * }
		 */
	}

}
