package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class NatCalIdEntity implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	@Column(name = "TNC_YEAR")
	private int year;
	
	@Column(name = "TNC_MONTH")
	private int month;
	
	@Column(name = "TNC_DAY")
	private int day;
	
	
	









}
