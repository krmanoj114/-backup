package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class HaisenContainerResponseDTO implements Serializable{
	
	
	private static final long serialVersionUID = 1L;
	private String planVanDate;
	private String contDest;
	private String shippingComp;

}
