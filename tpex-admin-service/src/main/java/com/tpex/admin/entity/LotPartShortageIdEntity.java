package com.tpex.admin.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class LotPartShortageIdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "ETD")
	private Date etd;

	@Column(name = "DST_CD")
	private String dstCd;

	@Column(name = "ORD_NO")
	private String ordNo;

	@Column(name = "PKG_MTH")
	private String pkgMth;

	@Column(name = "CF_CD")
	private String cfCd;

	@Column(name = "LOT_MOD_NO")
	private String lotModNo;

	@Column(name = "CASE_NO")
	private String caseNo;

	@Column(name = "BOX_NO")
	private String boxNo;

	@Column(name = "PART_NO")
	private String partNo;

	@Column(name = "REV_NO")
	private String revNo;
}
