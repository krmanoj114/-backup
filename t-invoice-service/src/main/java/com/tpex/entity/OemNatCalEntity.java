package com.tpex.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
@Table(name = "	OEM_NAT_CAL")
@Entity
public class OemNatCalEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	@Column(name = "NC_MTH")
	private String ncMonth;
	
	@Column(name = "NC_WRK_DAY")
	private String ncWorkDay;
	
	@Column(name = "NC_UPD_BY")
	private String ncUpdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern="dd-MM-yyyy")
	@Column(name = "NC_UPD_DT")
	private Date ncUpdDt;
	

}
