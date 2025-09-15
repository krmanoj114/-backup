package com.tpex.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data
@Entity

/**
 * Instantiates a new oem lot prc mst entity.
 *
 * @param priceCFCode the price CF code
 * @param priceDestCode the price dest code
 * @param priceLotCode the price lot code
 * @param priceCurrCode the price curr code
 * @param lotPrice the lot price
 * @param updateBy the update by
 * @param updateDate the update date
 * @param effFromMonth the eff from month
 * @param effFromToMonth the eff from to month
 * @param lotPriceStatus the lot price status
 * @param companyCode the company code
 */
@AllArgsConstructor

/**
 * Instantiates a new oem lot prc mst entity.
 */
@NoArgsConstructor
@Table(name="TB_M_LOT_PRICE")
@IdClass(OemLotPrcMstEntityID.class)
public class OemLotPrcMstEntity {
	
	/** The price CF code. */
	@Id
	@Column(name = "CF_CD")
	private String priceCFCode;
	
	/** The price dest code. */
	@Id
	@Column(name = "DST_CD")
	private String priceDestCode;
	
	/** The price lot code. */
	@Id
	@Column(name = "LOT_CD")
	private String priceLotCode;
	
	/** The price curr code. */
	@Id
	@Column(name = "CURR_CD")
	private String priceCurrCode;
	
	/** The lot price. */
	@Column(name = "LOT_PRC")
	private Double lotPrice;
	
	/** The update by. */
	@Column(name = "UPD_BY")
	private String updateBy;
	
	/** The update date. */
	@Column(name = "UPD_DT")
	private Date updateDate;
	
	/** The eff from month. */
	@Id
	@Column(name = "EFF_FR_MTH")
	private String effFromMonth;
	
	/** The eff from to month. */
	@Column(name = "EFF_TO_MTH")
	private String effFromToMonth;
	
	/** The lot price status. */
	@Column(name = "LOT_PRC_STS")
	private String lotPriceStatus;

	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;
}
