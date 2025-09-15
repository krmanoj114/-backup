package com.tpex.month.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_M_CUSTOM_BROCKER")
public class CustomBrokerMasterEntity {

	@Id
	@Column(name = "CB_CD")
	private String cbmCbCd;
	@Column(name = "CB_NM")
	private String cbmCbNm;
	@Column(name = "FIRST_FILE_NM")
	private String cbmFirstFileNm;
	@Column(name = "SECOND_FILE_NM")
	private String cbmSecondFileNm;
	@Column(name = "THRD_FILE_NM")
	private String cbmThrdFileNm;
	@Column(name = "FOURTH_FILE_NM")
	private String cbmFourthFileNm;
	@Column(name = "CMP_CD")
	private String cmpCd;

}
