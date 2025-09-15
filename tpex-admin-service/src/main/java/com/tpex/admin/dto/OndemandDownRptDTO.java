package com.tpex.admin.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OndemandDownRptDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String reportName;

	private String status;

	private String downLoc;

	private String createBy;

	private String createDate;

}
