package com.tpex.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name = "TB_M_SHIPPING_CTRL")
@Entity
public class OemShippingCtrlMstEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private OemShippingCtrlMstIdEntity id;

	@Column(name = "goods_desc_1")
	private String shCtrlGoodsDesc1;

	@Column(name = "goods_desc_2")
	private String shCtrlGoodsDesc2;

	@Column(name = "goods_desc_6")
	private String shCtrlGoodsDesc3;

	@Column(name = "buyer")
	private String shCtrBuyer;

	@Column(name = "prod_grp_cd")
	private String shCtrlProdGrpCd;

	@Column(name = "folder_nm")
	private String shCtrlFolderNm;

	@Column(name = "cnsg")
	private String shCtrlCnsg;

	@Column(name = "ntf_party_dtls")
	private String shCtrlNtfPartyDtls;

	@Column(name = "trade_term_cd")
	private String shCtrlTradeTermCd;

	@Column(name = "cert_origin_rep")
	private String shCtrlCertOriginRep;

	@Column(name = "sold_messrs")
	private String shCtrlSoldMessrs;

	@Column(name = "pls_flag")
	private String shCtrlPlsFlag;

	@Column(name = "upd_by")
	private String shCtrlUpdBy;

	@Column(name = "upd_dt")
	private Timestamp shCtrlUpdDt;
}
