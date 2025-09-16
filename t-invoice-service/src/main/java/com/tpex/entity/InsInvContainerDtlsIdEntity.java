package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import lombok.Data;

@Data
@Embeddable
public class InsInvContainerDtlsIdEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "INV_NO")
	private String invoiceNo;
	
	@Column(name="CONT_DST")
	private String contDest;

	@Column(name="CONT_SNO")
	private String contSno;

}
