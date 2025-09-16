package com.tpex.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Table(name= "TB_M_BUYER_SELLER_DETAIL")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class BuyerMasterEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "CD")
	private String buyerCode;
	
	@Column(name = "NM")
	private String buyerName;
	
	@Column(name = "UPD_BY")
	private String updatedBY;
	
	@Column(name = "UPD_DT")
	private String updatedDate;
	
	@Column(name = "CMP_CD")
	private String cmpCd;
	
}
