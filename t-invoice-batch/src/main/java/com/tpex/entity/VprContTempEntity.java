package com.tpex.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@IdClass(VprContTempId.class)
@Table(name = "TB_S_VPR_CONTAINER")
public class VprContTempEntity {

	@Id
	@Column(name = "PROC_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime procDt;
	@Id
	@Column(name = "INTR_ID", nullable = false)
	private String intrId;
	@Id
	@Column(name = "MAIN_FLG", nullable = false)
	private String mainFlag;
	@Id
	@Column(name = "CONT_DST_CD", nullable = false)
	private String contDestinationCode;
	@Column(name = "RECV_PLNT")
	private String recvPlant;
	@Id
	@Column(name = "CONT_SNO", nullable = false)
	private String contSno;
	@Column(name = "SEAL_NO")
	private String sealNo;
	@Column(name = "VAN_PLNT_CD", nullable = false)
	private String vanPlantCode;
	@Column(name = "VAN_PLNT_NM")
	private String vanPlantName;
	@Column(name = "ISO_CONT_NO")
	private String isoContNo;
	@Column(name = "PLN_VAN_STRT_DT", nullable = false, columnDefinition = "DATE")
	private LocalDate planVanStartDate;
	@Column(name = "PLN_VAN_STRT_TM")
	private String planVanStartTime;
	@Column(name = "PLN_VAN_END_DT", columnDefinition = "DATE")
	private LocalDate planVanEndDate;
	@Column(name = "PLN_VAN_END_TM")
	private String planVanEndTime;
	@Column(name = "ACT_VAN_DT")
	private LocalDate actVanDate;
	@Column(name = "ACT_VAN_TM")
	private String actVanTime;
	@Column(name = "VAN_LN")
	private String vanLn;
	@Column(name = "VAN_SEQ_NO")
	private Integer vanSeqNo;
	@Column(name = "PAC_TBL_1")
	private Integer pacTbl1;
	@Column(name = "VAN_GRP_CD")
	private String vanGroupCode;
	@Column(name = "VAN_GRP_SEQ")
	private Integer vanGroupSeq;
	@Column(name = "TRNS")
	private String trns;
	@Column(name = "CONT_TYP", nullable = false)
	private String contType;
	@Column(name = "CONT_SIZE", nullable = false)
	private String contSize;
	@Column(name = "CONT_GRP_CD")
	private String contGroupCode;
	@Column(name = "CONT_NET_WT", nullable = false, precision = 8, scale = 3)
	private BigDecimal contNetWeight;
	@Column(name = "CONT_GROSS_WT", precision = 8, scale = 3)
	private BigDecimal contGrossWeight;
	@Column(name = "CONT_TARE_WT")
	private Integer contTareWeight;
	@Column(name = "CONT_MAX_WT")
	private Integer contMaxWeight;
	@Column(name = "CONT_MAX_COF_WT")
	private String contMaxCofWeight;
	@Column(name = "CONT_MAX_M3")
	private Integer contMaxM3;
	@Column(name = "CONT_MAX_COF_M3")
	private String contMaxCofM3;
	@Column(name = "XDOC_FLG")
	private String xdocFlag;
	@Column(name = "DEP_PORT")
	private String depPort;
	@Column(name = "DST_PORT")
	private String dstPort;
	@Column(name = "VSSL_SRL_1")
	private String vsslSrl1;
	@Column(name = "VSSL_NM_1")
	private String vsslName1;
	@Column(name = "SHP_NM_1")
	private String shipName1;
	@Column(name = "VOY_1")
	private String voy1;
	@Column(name = "ETD_1", columnDefinition = "DATE")
	private LocalDate etd1;
	@Column(name = "ETA_1", columnDefinition = "DATE")
	private LocalDate eta1;
	@Column(name = "VSSL_SRL_2")
	private String vsslSrl2;
	@Column(name = "VSSL_NM_2")
	private String vsslName2;
	@Column(name = "SHP_NM_2")
	private String shipName2;
	@Column(name = "VOY_2")
	private String voy2;
	@Column(name = "ETD_2")
	private LocalDate etd2;
	@Column(name = "ETA_2")
	private LocalDate eta2;
	@Column(name = "DST_NM")
	private String dstName;
	@Column(name = "UPD_BY", nullable = false)
	private String updateBy;
	@Column(name = "UPD_DT", nullable = false, columnDefinition = "TIMESTAMP")
	private LocalDateTime updateDate;
	@Column(name = "CMP_CD")
	private String compCode;
}
