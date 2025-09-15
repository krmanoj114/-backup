package com.tpex.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class BrokerDetailsDTO implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String brokerCode;
	
	private String brokerName;
	
}
