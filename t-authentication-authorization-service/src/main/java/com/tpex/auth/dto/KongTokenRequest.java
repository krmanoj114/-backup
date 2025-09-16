package com.tpex.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class KongTokenRequest {
	
	private String client_id;
	private String client_secret;
	private String grant_type;
	private String provision_key;
	private String authenticated_userid;
	private String scope;

}
