package com.tpex.admin.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.admin.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_CNSG")
@Entity
public class AddressMasterEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AddressMasterIdEntity id;
	
	@Column(name = "NAME")
	private String name;

	@Column(name = "ADD_1")
	private String address1;

	@Column(name = "ADD_2")
	private String address2;

	@Column(name = "ADD_3")
	private String address3;
	
	@Column(name = "ADD_4")
	private String address4;
	
	@Column(name = "ZIP")
	private String zip;

	@Column(name = "SHRT_NM")
	private String shortName;

	@Column(name = "SAP_CD")
	private String sapCode;

	@Column(name = "CNSG_CD")
	private String consigneeCode;
	
	@Column(name = "CNTRY_CD")
	private String countryCode;
	
	@Column(name = "TEL_NO")
	private String telephoneNumber;

	@Column(name = "FAX_NO")
	private String faxNumber;

	@Column(name = "EMAIL")
	private String email;

	@Column(name = "TELEX")
	private String telexNumber;
	
	@Column(name = "CONTACT")
	private String contactPerson;
	
	@Column(name = "INV_ADD_FLG")
	private String invoiceFlag;

	@Column(name = "SC_INV_CNTRY_FLG")
	private String scFlag;

	@Column(name = "SC_REMARKS")
	private String scRemarks;

	@Column(name = "SC_AUTH_EMP1")
	private String employee1;
	
	@Column(name = "SC_AUTH_EMP2")
	private String employee2;
	
	@Column(name = "SC_AUTH_EMP3")
	private String employee3;

	@Column(name = "UPD_BY")
	private String updateBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Timestamp updateDate;

	public LocalDateTime getUpdDt() {
		return DateUtil.convertSqlDateToLocalDateTimeOfEntityAttribute(updateDate);
	}
	
	@Column(name = "CMP_CD")
	private String cmpCode;
	






}
