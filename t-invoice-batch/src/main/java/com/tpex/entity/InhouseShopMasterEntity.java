package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="TB_M_INH_SHOP")
public class InhouseShopMasterEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="INS_SHOP_CD")
	private String insShopCd;
	
	@Column(name="DESCRIPTION")
	private String desc;
	
	@Column(name="UPD_BY")
	private String updateBy;
	
	@Column(name = "UPD_DT")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date updatedDate;
	public LocalDate getUpdatedDate() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(updatedDate);
	}
	
	@Column(name="INHOUSE_PLANT")
	private String inhousePlant;
	
	@Column(name="CMP_CD")
	private String companyCode;

}
