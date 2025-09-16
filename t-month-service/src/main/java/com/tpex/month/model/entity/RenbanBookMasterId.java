package com.tpex.month.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class RenbanBookMasterId implements Serializable {

	private static final long serialVersionUID = -628412931947263995L;
	
	private String vanningMonth;
	private String destinationCode;
	private LocalDate etd1;
	private String shippingCompany;
	private String groupId;
}
