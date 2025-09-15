package com.tpex.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OAuth2AzureTokenResponse {

	private String token_type;
	private String scope;
	private String expires_in;
	private String ext_expires_in;
	private String access_token;
}
