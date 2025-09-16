package com.tpex.admin.module;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class OndemandDownRptModule implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String reportName;

	private String status;

	private String downLoc;
	private String createBy;

	private String createDate;

	private String updateBy;
	private Date updateDate;

}
