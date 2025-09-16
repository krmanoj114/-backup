package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "TB_M_CST_LBL")
public class CustomLabelEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "LBL_CD")
	private String labelCode;

	@Column(name = "LBL_DESC")
	private String labelDescription;

	@Column(name = "LBL_VALUE")
	private String labelValue;

	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	private String updatedDate;

	@Column(name = "CMP_CD")
	private String cmpCode;
	
	@Column(name = "BARCODE_VALUE")
	private String barcodeInputRequired;

}
