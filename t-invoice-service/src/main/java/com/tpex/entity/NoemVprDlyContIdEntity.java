package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class NoemVprDlyContIdEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "NVDC_CONT_DST_CD")
	private String contDstCd;
	
	@Column(name = "NVDC_CONT_SNO")
	private String contSno;
}
