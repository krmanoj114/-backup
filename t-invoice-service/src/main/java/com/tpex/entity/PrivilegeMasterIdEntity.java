package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;


import lombok.Data;

@Data
@Embeddable
public class PrivilegeMasterIdEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "PRIV_CD")
	private String privilegeCode;

	@Column(name = "PRIV_NAME")
	private String privilegeName;

}
