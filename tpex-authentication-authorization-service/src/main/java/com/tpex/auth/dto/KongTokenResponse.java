package com.tpex.auth.dto;

import lombok.Data;

@Data
public class KongTokenResponse {
	private String refresh_token;
	private String token_type;
	private String access_token;
	private String expires_in;
}
