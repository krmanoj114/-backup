package com.tpex.batchjob.binf023;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Instantiates a new binf 023 output dto.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Binf023OutputDto {

    private String recordId;

    private String packingMth;

    private String requireDt;

    private String containerSize;

    private String kCST2DelryDt;

    private String insDt;

    private String etd1;

    private String shpCmpnyCd;

    private String shpCmpnyNm;

    private String bookNo;

    private String vesselNm1;

    private String renbanCd;

    private String destCd;

    private String destName;

    private String vanningPlant;

    private String remark;
}
