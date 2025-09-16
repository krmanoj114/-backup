package com.tpex.barcode.bean;

import org.springframework.stereotype.Component;

@Component
public class KafkaMessage {
	private String gunId;
	private String scanData;
	private String userId;

	public String getGunId() {
		return gunId;
	}

	public void setGunId(String gunId) {
		this.gunId = gunId;
	}

	public String getScanData() {
		return scanData;
	}

	public void setScanData(String scanData) {
		this.scanData = scanData;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	

}
