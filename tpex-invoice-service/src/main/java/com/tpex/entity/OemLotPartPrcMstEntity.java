package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="TB_M_LOT_PART_PRICE")
@IdClass(OemLotPartPrcMstEntityID.class)
public class OemLotPartPrcMstEntity {
	
	@Id
	@Column(name="CF_CD")
	private String partPricericeCFCode;
	
	@Id
	@Column(name="DST_CD")
	private String partPriceDestCode;
	
	@Id
	@Column(name="LOT_CD")
	private String partPriceLotCode;
	
	@Id
	@Column(name="CURR_CD")
	private String partPriceCurrCode;
	
	@Id
	@Column(name="PART_NO")
	private String partPriceNo;
	
	@Column(name="PART_PRC")
	private Double partPricePrc;
	
	@Column(name="UPD_BY")
	private String partPriceUpdateBy;
	
	@Column(name="UPD_DT")
	private String partPriceUpdateDate;
	
	@Column(name="PART_NM")
	private String partPriceName;
	
	@Id
	@Column(name="EFF_FR_MTH")
	private String partPriceEffFromMonth;
	
	@Column(name="EFF_TO_MTH")
	private String partPriceEffToMonth;
	
	@Column(name="PUSAGE")
	private Double partPriceUsage;
	
	@Column(name = "CMP_CD")
	private String companyCode;
	
	
}
