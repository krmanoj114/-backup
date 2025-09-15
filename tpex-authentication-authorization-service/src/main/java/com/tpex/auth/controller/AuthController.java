package com.tpex.auth.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.auth.dto.AuthInfo;
import com.tpex.auth.dto.AuthResponse;
import com.tpex.auth.dto.KongTokenRequest;
import com.tpex.auth.dto.KongTokenResponse;
import com.tpex.auth.dto.MessageErrorSC2;
import com.tpex.auth.dto.OAuth2AzureTokenResponse;
import com.tpex.auth.dto.Sc2AuthRequest;
import com.tpex.auth.dto.Sc2AuthResponse;
import com.tpex.auth.dto.UserDTO;
import com.tpex.auth.dto.UserInfo;
import com.tpex.auth.service.AuthService;
import com.tpex.auth.service.JwtUserDetailsService;
import com.tpex.auth.util.ConstantUtils;
import com.tpex.auth.util.InitialValueUtil;
import com.tpex.auth.util.JwtTokenUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/auth")
public class AuthController {
	
	private final JwtTokenUtil jwtTokenUtil;
	private final JwtUserDetailsService jwtUserDetailsService;
	private final AuthenticationManager authenticationManager;
	private final InitialValueUtil tpexConfigUtilAuth;
	private final AuthService authService;
	private RestTemplate restTemplate = new RestTemplate();
	
	//for test display login with azure
	@GetMapping(path = "/login")
	public String login() {
		return "Login with Azure AD <a href=\""
				+ tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.BASE_URL_AUTH)
				+ tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.TENANT_ID)
				+ tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.ENDPOINT_AUTHORIZE)
				+ "?client_id="+tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.CLIENT_ID)
				+ "&scope="+tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.SCOPE)
				+ "&redirect_uri="+tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.REDIRECT_URI)
				+ "&response_type=code\">click</a>";
	}
	
	/**
	 * Login with SC2 and can register with put uniqueName value.
	 *
	 * @param request the request
	 * @return accessToken
	 * @throws UsernameNotFoundException the username not found exception
	 */
	@PostMapping(path = "/loginSC2")
	public AuthResponse loginSC2(@Valid @RequestBody Sc2AuthRequest request) throws UsernameNotFoundException {
		
		KongTokenResponse kongResponse = callKong();
		
		ResponseEntity<Sc2AuthResponse> sc2Response = callSC2(request, kongResponse);
		AuthResponse res = new AuthResponse();
		prepareResponse(request, sc2Response, res);
		return res;
	}

	private void prepareResponse(Sc2AuthRequest request, ResponseEntity<Sc2AuthResponse> sc2Response, AuthResponse res)
			throws UsernameNotFoundException {
		if (sc2Response == null || sc2Response.getBody() == null) {
			res.setErrorMsg("Unexpected error.");
			return;
		}
		Sc2AuthResponse body = sc2Response.getBody();
		if (!"MSC20042AINF".equals(body.getMessageId())) {
			res.setError(new MessageErrorSC2(body.getMessageId()));
		} else {
			UserInfo userInfo = body.getUserInfo();
			userInfo.setUniqueName(request.getUniqueName());
			
			jwtUserDetailsService.createOrUpdateUser(userInfo, body.getAuthInfo());
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(userInfo.getUserId());

			settingResponse(res, userDetails);
		}

	}
	
	private void settingResponse(AuthResponse res, UserDetails userDetails) {
		authenticate(userDetails.getUsername(), ConstantUtils.DUMMY_PASS);
		if(userDetails instanceof UserDTO) {
			UserDTO userDto = (UserDTO) userDetails;
			res.setUserFirstName(userDto.getFirstName());
			res.setCompanyDefault(userDto.getCompanyCode().equals("tmap-em") ? "tmt" : userDto.getCompanyCode());
		}
		res.setUserId(userDetails.getUsername());
		res.setCompanyDropdown(authService.getCompanyAndPlantDropdownByUserId(userDetails.getUsername()));
		res.setFunctionList(authService.getFunctionPermissionByUserId(userDetails.getUsername()));
		res.setToken(jwtTokenUtil.generateToken(userDetails));
	}

	/**
	 * Call back from Azure AD.
	 *
	 * @param params from azure
	 * @param response the response
	 * @param request the request
	 * @return accessToken or uniqueName to register
	 * @throws JsonProcessingException the json processing exception
	 */
	@GetMapping(path = "/callback")
	public AuthResponse callbackAzure(@RequestParam Map<String, Object> params, HttpServletResponse response, HttpServletRequest request) throws JsonProcessingException  {
		
		String code = (String) params.get("code");
		
		MultiValueMap<String, String> paramForToken = new LinkedMultiValueMap<>();
		paramForToken.add("code", code);
		paramForToken.add("grant_type", "authorization_code");
		paramForToken.add("redirect_uri", tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.REDIRECT_URI));
		paramForToken.add("client_id", tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.CLIENT_ID));
		paramForToken.add("client_secret", tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.CLIENT_SECRET));
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		HttpEntity<?> entity = new HttpEntity<>(paramForToken, headers);
		
		RestTemplate rest = new RestTemplate();
		ResponseEntity<String> strResponse = rest.exchange(
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.BASE_URL_AUTH)
						+ tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.TENANT_ID)
						+ tpexConfigUtilAuth.getConfigValue(ConstantUtils.AzureConfig.ENDPOINT_TOKEN),
				HttpMethod.POST, entity, String.class);
		
		ObjectMapper objMapper = new ObjectMapper();
		OAuth2AzureTokenResponse tokenResponse = objMapper.readValue(strResponse.getBody(), OAuth2AzureTokenResponse.class);
		
		Jwt<Header, Claims> decodeToken = jwtTokenUtil.decodeToken(tokenResponse.getAccess_token());
		String uniqueName = String.valueOf(decodeToken.getBody().get("unique_name"));
		//uniqueName is email e.g. "Peeranut.K@Coforge.com"
		
		UserDetails userDetails = null;
		AuthResponse res = new AuthResponse();
		try {
			userDetails = jwtUserDetailsService.loadUserByUniqueName(uniqueName);
		} catch (UsernameNotFoundException e) {
			res.setUniqueName(uniqueName);
			res.setErrorCd("AZURENOTFOUND");
			res.setErrorMsg("Redirect to register for activate Azure AD");
			return res;
		}
		
		settingResponse(res, userDetails);
		return res;
	}
	
	private void authenticate(String username, String password) throws AuthenticationException {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new DisabledException("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new BadCredentialsException("INVALID_CREDENTIALS", e);
		}
	}
	
	private KongTokenResponse callKong() {
		KongTokenRequest kongRequest = new KongTokenRequest(
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.CLIENT_ID),
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.CLIENT_SECRET),
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.GRANT_TYPE),
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.PROVISION_KEY),
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.AUTHENTICATED_USER_ID),
				tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.SCOPE));

		return restTemplate.postForObject(tpexConfigUtilAuth.getConfigValue(ConstantUtils.KongConfig.URL), kongRequest, KongTokenResponse.class);
	}
	
	private ResponseEntity<Sc2AuthResponse> callSC2(Sc2AuthRequest request, KongTokenResponse kongResponse) {
		if(kongResponse == null || kongResponse.getAccess_token() == null) {
			return null;
		}
		HttpHeaders header = new HttpHeaders();
		header.add(ConstantUtils.AUTHORIZATION_HEADER, kongResponse.getAccess_token());
		request.setSystemDn(tpexConfigUtilAuth.getConfigValue(ConstantUtils.SC2Config.SYSTEM_DOMAIN));
		
		HttpEntity<Sc2AuthRequest> entity = new HttpEntity<>(request, header);
		
		return restTemplate.exchange(tpexConfigUtilAuth.getConfigValue(ConstantUtils.SC2Config.AUTH_URL), HttpMethod.POST, entity, Sc2AuthResponse.class);
	}
	
}
