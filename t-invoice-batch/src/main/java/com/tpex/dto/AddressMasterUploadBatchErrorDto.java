package com.tpex.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new address master upload batch dto.
 */
@Data

/**
 * Instantiates a new address master upload batch error dto.
 *
 * @param companyCode the company code
 * @param branch the branch
 * @param companyName the company name
 * @param address1 the address 1
 * @param address2 the address 2
 * @param address3 the address 3
 * @param address4 the address 4
 * @param zipCode the zip code
 * @param shortName the short name
 * @param sapAcCode the sap ac code
 * @param consignee the consignee
 * @param countryCode the country code
 * @param telephoneNo the telephone no
 * @param faxNo the fax no
 * @param email the email
 * @param telexNo the telex no
 * @param contactPerson the contact person
 * @param defaultInvoiceAddress the default invoice address
 * @param scFlag the sc flag
 * @param scRemark the sc remark
 * @param employee1 the employee 1
 * @param employee2 the employee 2
 * @param employee3 the employee 3
 * @param errorReason the error reason
 */
@AllArgsConstructor

/**
 * Instantiates a new address master upload batch error dto.
 */
@NoArgsConstructor
public class AddressMasterUploadBatchErrorDto implements Serializable {

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
	
	/** The error reason. */
	private String errorReason;

}
