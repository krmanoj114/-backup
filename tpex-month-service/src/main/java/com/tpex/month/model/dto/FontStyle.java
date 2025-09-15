package com.tpex.month.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FontStyle {

	private int fontSize;
	private boolean bold;
	private String fontFamily;
}
