package com.tpex.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The Class OemLotSizeMstEntity.
 */
@Entity

/**
 * To string.
 *
 * @return the java.lang. string
 */
@Data

/**
 * Instantiates a new oem lot size mst entity.
 *
 * @param lotModImp the lot mod imp
 * @param carFamilyCode the car family code
 * @param lotSizeCode the lot size code
 * @param lotCode the lot code
 * @param partNumber the part number
 * @param companyCode the company code
 */
@AllArgsConstructor

/**
 * Instantiates a new oem lot size mst entity.
 */
@NoArgsConstructor
@NotEmpty
@Table(name = "TB_M_LOT_SIZE")
@IdClass(OemLotSizeMstIDEntity.class)
public class OemLotSizeMstEntity {
	
	/** The lot mod imp. */
	@Id
	@Column(name = "MOD_IMP_CD")
	private String lotModImp;
	
	/** The car family code. */
	@Id
	@Column(name = "CF_CD")
	private String carFamilyCode;
	
	/** The lot size code. */
	@Column(name = "LOT_SIZE")
	private Double lotSizeCode;
	
	/** The lot code. */
	@Id
	@Column(name = "LOT_CD")
	private String lotCode;
	
	/** The part number. */
	@Id
	@Column(name = "PART_NO")
	private String partNumber;
	
	/** The company code. */
	@Column(name = "CMP_CD")
	private String companyCode;

}
