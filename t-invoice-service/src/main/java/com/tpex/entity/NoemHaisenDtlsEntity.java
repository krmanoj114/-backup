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

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_R_HAISEN_D")
@Entity
public class NoemHaisenDtlsEntity implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private NoemHaisenDtlsIdEntity id;

	@Column(name = "HAISEN_YEAR_MTH")
	private String haisenYearMonth;

	@Column(name = "HAISEN_NO")
	private String haisenNo;

	@Column(name = "ETA")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date etaDate;
	public LocalDate getEtaDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(etaDate);
	}

	@Column(name = "VSSL_NM_FEEDER")
	private String vesselFeeder;
	
	@Column(name = "VOY_NM_FEEDER")
	private String voyageFeeder;

	@Column(name = "SHIP_CO_NM")
	private String shipCoNM;

	@Column(name = "NO_OF_20FT")
	private Integer noOf20FtContainer;

	@Column(name = "NO_OF_40FT")
	private Integer noOf40FtContainer;

	@Column(name = "CONT_EFF")
	private BigDecimal containerEffeciency;

	@Column(name = "LCL_VOL")
	private Integer lclVol;

	@Column(name = "UPD_BY")
	private String updatedBy;

	@Column(name = "UPD_DT")
	private Date updatedDate;
	
	@Column(name = "CMP_CD")
	private String companyCode;

}
