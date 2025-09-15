package com.tpex.dto;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tpex.util.ConstantUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoemHaisenDetailDTO implements Serializable {

	private static final long serialVersionUID = 1L;


	private String haisenYearMonth;
	private String haisenNo;
	
	private String etd;
	
	private String eta;
	private String buyer;
	
	private String portOfLoading;
	
	private String portOfDischarge;
	
	private String oceanVessel;
	
	private String oceanVoyage;
	private List<String> feederVessel;
	private List<String> feederVoyage;
	
	private String shipCompName;
	private Integer noOf20ftContainer;
	private Integer noOf40ftContainer;
	private String containerEfficiency;


}
