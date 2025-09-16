package com.tpex.daily.entity;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.daily.util.DateUtil;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_M_ISO_CONTAINER")
@Entity
public class IsoContainerMasterEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@EmbeddedId
	private IsoContainerMasterIdEntity id;
    
	@Column(name="ISO_CONT_NO")
	private String isoContainerNo;

	@Column(name="SHP_NM_1")
	private String shipName;
	
	@Column(name="CONT_TYP")
	private String conatinerType;

	@Column(name="CONT_SIZE")
	private String containerSize;
	
	@Column(name="UPD_BY")
	private String updatedBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}
	
	@Column(name="Seal_No")
	private String sealNo;
	
	@Column(name="Tare_Weight")
	private BigInteger tareWeight;

	@Column(name="CMP_CD")
	private String companyCode;
	

}
