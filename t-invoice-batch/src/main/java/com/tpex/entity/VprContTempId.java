package com.tpex.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@Data
public class VprContTempId implements Serializable {

	private static final long serialVersionUID = -7879543238454534328L;

	private LocalDateTime procDt;
	private String intrId;
	private String mainFlag;
	private String contDestinationCode;
	private String contSno;
}
