package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * Instantiates a new address master upload batch dto.
 */
@Data
public class AddressMasterUploadBatchDto implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The company code. */
	private String companyCode;
	
	/** The branch. */
	private String branch;
	
	/** The company name. */
	private String companyName;
	
	/** The address 1. */
	private String address1;
	
	/** The address 2. */
	private String address2;
	
	/** The address 3. */
	private String address3;
	
	/** The address 4. */
	private String address4;
	
	/** The zip code. */
	private String zipCode;
	
	/** The short name. */
	private String shortName;
	
	/** The sap ac code. */
	private String sapAcCode;
	
	/** The consignee. */
	private String consignee;
	
	/** The country code. */
	private String countryCode;
	
	/** The telephone no. */
	private String telephoneNo;
	
	/** The fax no. */
	private String faxNo;
	
	/** The email. */
	private String email;
	
	/** The telex no. */
	private String telexNo;
	
	/** The contact person. */
	private String contactPerson;
	
	/** The default invoice address. */
	private String defaultInvoiceAddress;
	
	/** The sc flag. */
	private String scFlag;
	
	/** The sc remark. */
	private String scRemark;
	
	/** The employee 1. */
	private String employee1;
	
	/** The employee 2. */
	private String employee2;
	
	/** The employee 3. */
	private String employee3;
	
	/** The company. */
	private String company;

}
