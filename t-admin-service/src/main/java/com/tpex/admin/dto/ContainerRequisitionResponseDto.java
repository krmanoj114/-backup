package com.tpex.admin.dto;

import java.io.Serializable;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContainerRequisitionResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String plantCd;
	private String vanPlntNm;
	private String shiftCd;
	private String vanSeq;
	private String reqDt;
	private String delvPlanTm;
	private String npcsVanFnlStTm;
	private String npcsVanFnlEndTm;
	private String actVanStrtTm;
	private String actVanEndTm;
	private int qty20;
	private int qty40;
	private String etdDt;
	private String shpComp;
	private String bookNo;
	private String vessNm;	
	private String cntIsoNo;
	private String sealNo;
	private String dstNm;
	private String dstCd;
	private String renbanCd;
	private String gpacClose;
	private double modQty;
	private String npcsDockNo;
	private String npcsCycTm;
	private String remark;	
	private String customBroker;
	private String conatainerTareWeight;
	private String conatainerNetWeight;	

}
