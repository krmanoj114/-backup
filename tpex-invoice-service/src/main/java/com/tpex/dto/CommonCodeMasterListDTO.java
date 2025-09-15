package com.tpex.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tpex.json.dto.ColumnsDTO;

import lombok.Data;

@Data
public class CommonCodeMasterListDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColumnsDTO columns;
	//List<Object[]> data = new ArrayList<>();
	List<Map<String, String>> data = new ArrayList<>();


}
