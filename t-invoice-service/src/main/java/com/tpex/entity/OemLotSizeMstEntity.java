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

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@NotEmpty
@Table(name = "TB_M_LOT_SIZE")
@IdClass(OemLotSizeMstIDEntity.class)
public class OemLotSizeMstEntity {

	@Id
	@Column(name = "MOD_IMP_CD")
	private String lotModImp;
	@Id
	@Column(name = "CF_CD")
	private String carFamilyCode;

	@Column(name = "LOT_SIZE")
	private Double lotSizeCode;
	@Id
	@Column(name = "LOT_CD")
	private String lotCode;
	@Id
	@Column(name = "PART_NO")
	private String partNumber;

	@Column(name = "CMP_CD")
	private String companyCode;

}
