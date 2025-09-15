package com.tpex.month.model.entity;

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
@IdClass(RenbanBookDetailId.class)
@Table(name = "TB_R_MTH_RENBAN_BOOKING_D")
public class RenbanBookDetailEntity {

	@Id
	@Column(name = "CONT_VAN_MTH")
	private String vanningMonth;
	@Id
	@Column(name = "CONT_DST_CD")
	private String destinationCode;
	@Id
	@Column(name = "ETD1", columnDefinition = "DATE")
	private LocalDate etd1;
	@Id
	@Column(name = "SHP_NM_1")
	private String shippingCompany;
	@Id
	@Column(name = "GROUP_ID")
	private String groupId;
	@Id
	@Column(name = "CONT_GRP_CD")
	private String containerGroupCode;
	@Column(name = "UPD_BY")
	private String updateBy;
	@Column(name = "UPD_DT", columnDefinition = "TIMESTAMP")
	private LocalDateTime updateDate;
	@Column(name = "CMP_CD")
	private String cmpCd;
	
}
