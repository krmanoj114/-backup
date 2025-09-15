package com.tpex.dto;

import java.io.Serializable;
import java.sql.Date;

import lombok.Data;

@Data
public class NatCalDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int year;

	private int month;
	
	private String monthName;
    
	private int day;
	
	private String dayName;
	
	private String whd;

	
	private String remarks;
    
	private Date crtDt;

	private Date updDt;
	
	private String crtBy;
    
	private String updBy;

   

}
