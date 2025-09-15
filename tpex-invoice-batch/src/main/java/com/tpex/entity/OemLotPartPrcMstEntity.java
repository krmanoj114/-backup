package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class OemLotPartPrcMstEntity.
 */
@Entity

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new oem lot part prc mst entity.
 *
 * @param partPricericeCFCode the part pricerice CF code
 * @param partPriceDestCode the part price dest code
 * @param partPriceLotCode the part price lot code
 * @param partPriceCurrCode the part price curr code
 * @param partPriceNo the part price no
 * @param partPricePrc the part price prc
 * @param partPriceUpdateBy the part price update by
 * @param partPriceUpdateDate the part price update date
 * @param partPriceName the part price name
 * @param partPriceEffFromMonth the part price eff from month
 * @param partPriceEffToMonth the part price eff to month
 * @param partPriceUsage the part price usage
 * @param companyCode the company code
 */
@AllArgsConstructor

/**
 * Instantiates a new oem lot part prc mst entity.
 */
@NoArgsConstructor
@Table(name="TB_M_LOT_PART_PRICE")
@IdClass(OemLotPartPrcMstEntityID.class)
public class OemLotPartPrcMstEntity {
	
	/** The part pricerice CF code. */
	@Id
	@Column(name="CF_CD")
	private String partPricericeCFCode;
	
	/** The part price dest code. */
	@Id
	@Column(name="DST_CD")
	private String partPriceDestCode;
	
	/** The part price lot code. */
	@Id
	@Column(name="LOT_CD")
	private String partPriceLotCode;
	
	/** The part price curr code. */
	@Id
	@Column(name="CURR_CD")
	private String partPriceCurrCode;
	
	/** The part price no. */
	@Id
	@Column(name="PART_NO")
	private String partPriceNo;
	
	/** The part price prc. */
	@Column(name="PART_PRC")
	private Double partPricePrc;
	
	/** The part price update by. */
	@Column(name="UPD_BY")
	private String partPriceUpdateBy;
	
	/** The part price update date. */
	@Column(name="UPD_DT")
	private String partPriceUpdateDate;
	
	/** The part price name. */
	@Column(name="PART_NM")
	private String partPriceName;
	
	/** The part price eff from month. */
	@Id
	@Column(name="EFF_FR_MTH")
	private String partPriceEffFromMonth;
	
	/** The part price eff to month. */
	@Column(name="EFF_TO_MTH")
	private String partPriceEffToMonth;
	
	/** The part price usage. */
	@Column(name="PUSAGE")
	private Double partPriceUsage;
	
	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;
	
}
