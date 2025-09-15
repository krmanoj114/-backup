package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class PartPriceMasterEntity.
 */
@Entity

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new part price master entity.
 *
 * @param id the id
 * @param partName the part name
 * @param partPrice the part prc
 * @param updateBy the update by
 * @param updateDate the update date
 * @param effToMonth the eff to month
 * @param cmpCd the cmp cd
 */
@AllArgsConstructor

/**
 * Instantiates a new part price master entity.
 */
@NoArgsConstructor
@Table(name="tb_m_part_price")
public class PartPriceMasterEntity implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The id. */
	@EmbeddedId
	private PartPriceMasterIdEntity id;
	
	/** The part name. */
	@Column(name="PART_NM")
	private String partName;
	
	/** The part price. */
	@Column(name="PART_PRC")
	private Double partPrice;
	
	/** The update by. */
	@Column(name="UPD_BY")
	private String updateBy;
	
	/** The update date. */
	@Column(name="UPD_DT")
	private String updateDate;
	
	/** The eff to month. */
	@Column(name="EFF_TO_MTH")
	private String effToMonth;
	
	/** The cmp cd. */
	@Column(name="CMP_CD")
	private String cmpCd = ConstantUtils.COMPANYNAME;
	
}
