package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.Data;

@Data
@Table(name = "TB_M_INV_WORK_PLAN")
@Entity
public class InvGenWorkPlanMstEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private InvGenWorkPlanMstIdEntity id;

	@Column(name = "ISSUE_INVOICE_DATE")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date issueInvoiceDate;
	public LocalDate getIssueInvoiceDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(issueInvoiceDate);
	}

	@Column(name = "ETD1")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd1;
	public LocalDate getEtd1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd1);
	}

	@Column(name = "ETA1")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta1;
	
	public LocalDate getEta1() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta1);
	}

	@Column(name = "CONT_20")
	private Integer cont20;

	@Column(name = "CONT_40")
	private Integer cont40;

	@Column(name = "VESSEL1")
	private String vessel1;

	@Column(name = "VOY1")
	private String voy1;

	@Column(name = "ETD2")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd2;
	public LocalDate getEtd2() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd2);
	}
	
	@Column(name = "ETA2")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta2;
	
	public LocalDate getEta2() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta2);
	}

	@Column(name = "VESSEL2")
	private String vessel2;

	@Column(name = "VOY2")
	private String voy2;

	@Column(name = "ETD3")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etd3;
	
	public LocalDate getEtd3() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etd3);
	}

	@Column(name = "ETA3")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta3;
	
	public LocalDate getEta3() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta3);
	}

	@Column(name = "VESSEL3")
	private String vessel3;

	@Column(name = "VOY3")
	private String voy3;

	@Column(name = "FOLDER_NAME")
	private String folderName;

	@Column(name = "PORT_OF_LOADING")
	private String portOfLoading;

	@Column(name = "PORT_OF_DISCHARGE")
	private String portOfDischarge;

	@Column(name = "CREATE_BY")
	private String createBy;

	@Column(name = "CREATE_DATE")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date createDate;
	public LocalDate getCreateDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(createDate);
	}

	@Column(name = "UPDATE_BY")
	private String updateBy;

	@Column(name = "UPDATE_DATE")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updateDate;
	public LocalDate getUpdateDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updateDate);
	}

	@Column(name = "DSI_TI_NO")
	private String dsiTiNo;

	@Column(name = "INV_GEN_FLAG")
	private String invGenFlag;

	@Column(name = "BROKER")
	private String broker;

	@Column(name = "CMP_CD")
	private String companyCode;

	@Column(name = "LINER")
	private String liner;

	@Column(name = "BOOKING_NO")
	private String bookingNo;

}
