package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class PartPriceMasterIdEntity.
 */
@Embeddable

/**
 * Instantiates a new part price master id entity.
 */
@Data

/**
 * Instantiates a new part price master id entity.
 */
@NoArgsConstructor

/**
 * Instantiates a new part price master id entity.
 *
 * @param cfCode the cf code
 * @param destCode the dest code
 * @param currencyCode the currency code
 * @param partNo the part no
 * @param effFromMonth the eff from month
 */
@AllArgsConstructor
public class PartPriceMasterIdEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The cf code. */
	@Column(name="CF_CD")
	private String cfCode;
	
	/** The dest code. */
	@Column(name="DST_CD")
	private String destCode;
	
	/** The part no. */
	@Column(name="PART_NO")
	private String partNo;
	
	/** The eff from month. */
	@Column(name="EFF_FR_MTH")
	private String effFromMonth;

}
