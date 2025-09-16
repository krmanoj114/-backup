package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new address master entity.
 */

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new address master entity.
 */
@NoArgsConstructor

/**
 * Instantiates a new address master entity.
 *
 * @param id the id
 * @param name the name
 * @param address1 the address 1
 * @param address2 the address 2
 * @param address3 the address 3
 * @param address4 the address 4
 * @param zip the zip
 * @param shortName the short name
 * @param sapCode the sap code
 * @param consigneeCode the consignee code
 * @param countryCode the country code
 * @param telephoneNumber the telephone number
 * @param faxNumber the fax number
 * @param email the email
 * @param telexNumber the telex number
 * @param contactPerson the contact person
 * @param invoiceFlag the invoice flag
 * @param scFlag the sc flag
 * @param scRemarks the sc remarks
 * @param employee1 the employee 1
 * @param employee2 the employee 2
 * @param employee3 the employee 3
 * @param updateBy the update by
 * @param updateDate the update date
 */
@AllArgsConstructor
@Table(name = "TB_M_CNSG")
@Entity
public class AddressMasterEntity implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@EmbeddedId
	private AddressMasterIdEntity id;
	
	/** The name. */
	@Column(name = "NAME")
	private String name;

	/** The address 1. */
	@Column(name = "ADD_1")
	private String address1;

	/** The address 2. */
	@Column(name = "ADD_2")
	private String address2;

	/** The address 3. */
	@Column(name = "ADD_3")
	private String address3;
	
	/** The address 4. */
	@Column(name = "ADD_4")
	private String address4;
	
	/** The zip. */
	@Column(name = "ZIP")
	private String zip;

	/** The short name. */
	@Column(name = "SHRT_NM")
	private String shortName;

	/** The sap code. */
	@Column(name = "SAP_CD")
	private String sapCode;

	/** The consignee code. */
	@Column(name = "CNSG_CD")
	private String consigneeCode;
	
	/** The country code. */
	@Column(name = "CNTRY_CD")
	private String countryCode;
	
	/** The telephone number. */
	@Column(name = "TEL_NO")
	private String telephoneNumber;

	/** The fax number. */
	@Column(name = "FAX_NO")
	private String faxNumber;

	/** The email. */
	@Column(name = "EMAIL")
	private String email;

	/** The telex number. */
	@Column(name = "TELEX")
	private String telexNumber;
	
	/** The contact person. */
	@Column(name = "CONTACT")
	private String contactPerson;
	
	/** The invoice flag. */
	@Column(name = "INV_ADD_FLG")
	private String invoiceFlag;

	/** The sc flag. */
	@Column(name = "SC_INV_CNTRY_FLG")
	private String scFlag;

	/** The sc remarks. */
	@Column(name = "SC_REMARKS")
	private String scRemarks;

	/** The employee 1. */
	@Column(name = "SC_AUTH_EMP1")
	private String employee1;
	
	/** The employee 2. */
	@Column(name = "SC_AUTH_EMP2")
	private String employee2;
	
	/** The employee 3. */
	@Column(name = "SC_AUTH_EMP3")
	private String employee3;

	/** The update by. */
	@Column(name = "UPD_BY")
	private String updateBy;

	/** The update date. */
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp updateDate;

	/**
	 * Gets the upd dt.
	 *
	 * @return the upd dt
	 */
	public LocalDateTime getUpdDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDate);
	}
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
