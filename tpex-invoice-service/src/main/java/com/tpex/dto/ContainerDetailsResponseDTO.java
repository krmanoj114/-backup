package com.tpex.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class ContainerDetailsResponseDTO implements Serializable{


	private static final long serialVersionUID = 1L;
	
	private String noOf20ftContainer;
	private String noOf40ftContainer;
	private String containerEfficiency;
	private Integer lclVolume;
	

}
