package com.tpex.month.model.dto;

import org.apache.poi.ss.usermodel.BorderStyle;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorderCustomStyle {

	private BorderStyle topStyle;
	private BorderStyle bottomStyle;
	private BorderStyle leftStyle;
	private BorderStyle rightStyle;
	private BorderColor borderColor;
}
