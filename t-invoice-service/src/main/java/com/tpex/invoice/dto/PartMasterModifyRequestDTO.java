package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import com.tpex.entity.PartMasterEntity;

import lombok.Data;

@Data
public class PartMasterModifyRequestDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<PartMasterEntity> data;

	private String userId;

}
