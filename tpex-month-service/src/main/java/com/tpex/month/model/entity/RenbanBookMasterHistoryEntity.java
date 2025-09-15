package com.tpex.month.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "TB_R_MTH_RENBAN_BOOKING_HISTORY")
public class RenbanBookMasterHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HST_ID")
	private Long historyId;
	@Column(name = "CONT_VAN_MTH")
	private String vanningMonth;
	@Column(name = "CONT_DST_CD")
	private String destinationCode;
	@Column(name = "ETD1", columnDefinition = "DATE")
	private LocalDate etd1;
	@Column(name = "VESSEL1")
	private String vessel1;
	@Column(name = "SHP_NM1")
	private String shippingCompany;
	@Column(name = "GROUP_ID")
	private String groupId;
	@Column(name = "ETA", columnDefinition = "DATE")
	private LocalDate eta;
	@Column(name = "BOOK_NO")
	private String bookingNo;
	@Column(name = "CB_FLG")
	private String cbFlag;
	@Column(name = "VAN_END_DT", columnDefinition = "TIMESTAMP")
	private LocalDateTime vanEndDate;
	@Column(name = "CB_CD")
	private String customBrokerCode;
	@Column(name = "UPD_BY")
	private String updateBy;
	@Column(name = "UPD_DT", columnDefinition = "TIMESTAMP")
	private LocalDateTime updateDate;
	@Column(name = "CONT_20")
	private Integer cont20;
	@Column(name = "CONT_40")
	private Integer cont40;
	@Column(name = "PREV_VESSEL1")
	private String prevVessel1;
	@Column(name = "PREV_BOOK_NO")
	private String prevBookingNo;
	@Column(name = "PREV_CB_CD")
	private String prevCustomBrokerCode;
	@Column(name = "RENBAN_CODE")
	private String renbanCode;
	@Column(name = "CMP_CD")
	private String cmpCd;
}
