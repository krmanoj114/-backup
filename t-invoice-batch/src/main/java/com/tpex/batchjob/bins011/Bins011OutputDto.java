package com.tpex.batchjob.bins011;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bins011OutputDto {

	private String recId;

	private String invNo;

	private String invDate;

	private String aicoFlg;

	private String invAmt;

	private String grossWt;

	private String partNo;

	private String netWt;

	private String noCases;

	private String qty;

	private String fob;

	private String cfCd;

	private String series;

	private String pckMth;

	private String coCd;

}
