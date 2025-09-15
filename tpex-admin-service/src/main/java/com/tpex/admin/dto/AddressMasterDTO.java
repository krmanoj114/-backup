package com.tpex.admin.dto;

import java.io.Serializable;
import java.sql.Timestamp;


import lombok.Data;

@Data
public class AddressMasterDTO implements Serializable{
	
	private String code;

	private String branch;
	
	private String name;

	private String address1;

	private String address2;

	private String address3;
	
	private String address4;
	
	private String zip;

	private String shortName;

	private String sapCode;

	private String consigneeCode;
	
	private String countryCode;
	
	private String telephoneNumber;

	private String faxNumber;

	private String email;

	private String telexNumber;
	
	private String contactPerson;
	
	private String invoiceFlag;

	private String scFlag;

	private String scRemarks;

	private String employee1;
	
	private String employee2;
	
	private String employee3;

	private String company;

	private String updateBy;

	private Timestamp updateDate;
	
	private String cmpCode;


}
