package com.tpex.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Sc2AuthResponse {

	private String messageId;
	private String dateTimeExecution;
	private String ticketId;
	private UserInfo userInfo;
	private List<AuthInfo> authInfo;
}
