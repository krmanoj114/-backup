package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class NatCalIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "YEAR")
	private int year;

	@Column(name = "MONTH")
	private int month;

	@Column(name = "DAY")
	private int day;

}
