package com.tpex.month.model.dto;

import org.apache.poi.xssf.usermodel.XSSFColor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorderColor {

	private XSSFColor top; 
	private XSSFColor bottom; 
	private XSSFColor left; 
	private XSSFColor right; 
}
