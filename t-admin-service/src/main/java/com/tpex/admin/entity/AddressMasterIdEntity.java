package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class AddressMasterIdEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name = "CD")
	private String code;

	@Column(name = "BRANCH")
	private String branch;
	
	@Column(name = "COMPANY")
	private String company;

}