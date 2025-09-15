package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotifyCodeDtoObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String indNotify;
	private String indNotifyName;
	private String indNotifyAddr1;
	private String indNotifyAddr2;
	private String indNotifyAddr3;
	private String indNotifyAddr4;

}
