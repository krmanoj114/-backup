package com.tpex.barcode.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Data
@Table(name="TB_M_Barcode_Data")
@IdClass(TpexBarcodeEntityPk.class)
public class TpexBarcodeEntity {
	@Id
	@Column(name = "Gun_id")
	private String gunId;
	@Id
	@Column(name = "Message_Body")
	private String messageBody;
	@Column(name = "Response_Body")
	private String respobseBody;
	@Column(name = "Serial_Number")
	private String serialNumber;
	
	
}
