package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class VprDlyVinTempId implements Serializable {

	private static final long serialVersionUID = 3392751039099611050L;

	private LocalDateTime procDt;
	private String intrId;
	private String lotCode;
	private String vinFrmNo;
}
