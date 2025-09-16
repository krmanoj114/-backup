package com.tpex.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Data;

@Embeddable
@Data
public class OemProcessCtrlIdEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "PRC_BATCH_ID ")
	private String batchId;

	@Column(name = "OEM_PROCESS_CTRL_ID ")
	private int processControlId;

}
