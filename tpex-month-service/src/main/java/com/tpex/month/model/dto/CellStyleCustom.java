package com.tpex.month.model.dto;

import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CellStyleCustom {

	private XSSFColor bgColor;
	private FontStyle fontStyle;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;
	private BorderCustomStyle borderStyle;
	private boolean wrapText;
}
