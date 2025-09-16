package com.tpex.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name = "TB_R_INV_CONTAINER_D")
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class InsInvContainerDtlsEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private InsInvContainerDtlsIdEntity id;
	
	@Column(name = "INV_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date invoiceDate;
	public LocalDate getInvoiceDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(invoiceDate);
	}

	@Column(name = "PLN_VAN_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date planVanningDate;
    public LocalDate getPlanVanningDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(planVanningDate);
	}

	@Column(name = "ACT_VAN_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date actVanningDate;

	public LocalDate getActVanningDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(actVanningDate);
	}


	@Column(name="SEAL_NO")
	private String sealNo;

	@Column(name="ISO_CONT_NO")
	private String isoContNo;

	@Column(name="TRNS_CD")
	private String trncCd;

	@Column(name="CONT_TYP")
	private String contType;

	@Column(name="CONT_SIZE")
	private String contSize;

	@Column(name="CONT_EFF")
	private Integer contEfficiency;

	@Column(name="UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date updatedDate;

	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}

	@Column(name="CONT_GRP_CD")
	private String contGrpCode;

	@Column(name="VAN_PLNT_CD")
	private String vanPlantCode;

	@Column(name="CONT_NET_WT")
	private BigDecimal contNetWeight;

	@Column(name="CONT_TARE_WT")
	private BigDecimal contTareWeight;

	@Column(name="CONT_GROSS_WT")
	private BigDecimal contGrossWeight;

	@Column(name="NO_OF_MOD")
	private Integer noOfModules;

	@Column(name="SUM_MOD_GROSS")
	private BigDecimal sumModGross;

	@Column(name="BOOK_NO")
	private String bookingNo;

	@Column(name="NO_OF_MOD_RRACK")
	private Integer noOfModuleRack;
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
