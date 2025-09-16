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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_M_LOT_PRICE")
@IdClass(OemLotPrcMstEntityID.class)
public class OemLotPrcMstEntity {

	@Id
	@Column(name = "CF_CD")
	private String priceCFCode;

	@Id
	@Column(name = "DST_CD")
	private String priceDestCode;

	@Id
	@Column(name = "LOT_CD")
	private String priceLotCode;

	@Id
	@Column(name = "CURR_CD")
	private String priceCurrCode;

	@Column(name = "LOT_PRC")
	private Double lotPrice;

	@Column(name = "UPD_BY")
	private String updateBy;

	@Column(name = "UPD_DT")
	private Date updateDate;

	@Id
	@Column(name = "EFF_FR_MTH")
	private String effFromMonth;

	@Column(name = "EFF_TO_MTH")
	private String effFromToMonth;

	@Column(name = "LOT_PRC_STS")
	private String lotPriceStatus;

	@Column(name = "CMP_CD")
	private String companyCode;
}
