package com.tpex.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "TB_M_PORT")
@Entity
public class OemPortMstEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "CD")
	private String cd;
	@Column(name = "NAME")
	private String name;
	@Column(name = "TH_NAME")
	private String thName;
	@Column(name = "UPD_BY")
	private String updBy;
	@Column(name = "UPD_DT")
	private Date updDt;
	@Column(name = "CMP_CD")
	private Date companyCode;
}
