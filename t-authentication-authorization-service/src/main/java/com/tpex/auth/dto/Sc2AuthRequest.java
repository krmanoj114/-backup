package com.tpex.auth.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class Sc2AuthRequest {

	private String uniqueName;
	private String userDomain;
	@NotBlank(message = "Username is mandatory")
	private String userId;
	@NotBlank(message = "Password is mandatory")
	private String userPassword;
	private String systemDn;
}
