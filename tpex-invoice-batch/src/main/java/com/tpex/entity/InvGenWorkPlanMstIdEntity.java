package com.tpex.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tpex.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class InvGenWorkPlanMstIdEntity implements Serializable{

	
	private static final long serialVersionUID = 1L;
	
	
	@Column(name = "ORIGINAL_ETD")
	@JsonFormat(pattern="dd/MM/yyyy")
	private Date originalEtd;
	
	public LocalDate getOriginalEtd() {
		return DateUtil.convertSqlDateToLocalDateOfEntityAttribute(originalEtd);
	}
	
	@Column(name = "CONT_DEST")
	private String contDest;
	
	@Column(name = "RENBAN_CODE")
	private String renbanCode;
	
}
