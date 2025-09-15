package com.tpex.auth.dto;

import java.util.List;

import lombok.Data;

@Data
public class FunctionPermissionDTO {

	private String functionName;
	private List<String> permission;

}
