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
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_R_MTH_RENBAN_BOOKING_H")
@Entity
public class NoemRenbanBookMstEntity implements Serializable{

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NoemRenbanBookMstIdEntity id;
    
	@Column(name="VESSEL1")
	private String vessel1;

	@Column(name = "ETA")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date eta;
	public LocalDate getEta() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(eta);
	}

	@Column(name="BOOK_NO")
	private String bookNo;

	@Column(name="CB_FLG")
	private String cbFlag;

	@Column(name = "VAN_END_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date vanEndDate;
	public LocalDate getVanEndDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(vanEndDate);
	}

	@Column(name="CB_CD")
	private String cbCd;

	@Column(name="UPD_BY")
	private String updatedBy;



	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}

	@Column(name="CONT_20")
	private String cont20;

	@Column(name="CONT_40")
	private String cont40;
	
	@Column(name="CMP_CD")
	private String companyCode;

}
