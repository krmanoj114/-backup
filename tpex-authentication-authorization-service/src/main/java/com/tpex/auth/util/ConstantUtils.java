package com.tpex.auth.util;

public class ConstantUtils {
	public static final String DUMMY_PASS = "password";
	public static final String DUMMY_PASS_BCRYPT = "$2a$10$38P1wR7Xn6.EmLFoHGPN5e33sWxB7D7/FB/GqlXDPulFfBjG599Dm";
	public static final String AUTHORIZATION_HEADER = "authorization";
	public static final String DELIMITER = "|";
	public static final String DELIMITER_ESCAPE = "\\|";
	
	// add Uri as needed
	private static final String[] SKIP_PATH = { "/auth/**", "/favicon.ico", "/v2/api-docs",
			"/configuration/ui", "/swagger-resources/**", "/configuration/security", "/swagger-ui.html",
			"/webjars/**" };
	
	public class KongConfig {
		private KongConfig() {}
		
		public static final String URL = "kong.url";
		public static final String CLIENT_ID = "kong.client-id";
		public static final String CLIENT_SECRET = "kong.client-secret";
		public static final String GRANT_TYPE = "kong.grant-type";
		public static final String PROVISION_KEY = "kong.provision-key";
		public static final String AUTHENTICATED_USER_ID = "kong.authenticated-user-id";
		public static final String SCOPE = "kong.scope";
	}
	
	public class SC2Config {
		private SC2Config() {}
		
		public static final String AUTH_URL = "sc2.auth-url";
		public static final String SYSTEM_DOMAIN = "sc2.system-domain";
	}
	
	public class AzureConfig {
		private AzureConfig() {}
		
		public static final String BASE_URL_AUTH = "azure.ad.baseurl";
		public static final String TENANT_ID = "azure.ad.tenant-id";
		public static final String CLIENT_ID = "azure.ad.client-id";
		public static final String CLIENT_SECRET = "azure.ad.client-secret";
		public static final String ENDPOINT_AUTHORIZE = "azure.ad.endpoint.authorize";
		public static final String ENDPOINT_TOKEN = "azure.ad.endpoint.token";
		public static final String REDIRECT_URI = "azure.ad.redirect-uri";
		public static final String SCOPE = "azure.ad.scope";
	}

	public static String[] getSkipPath() {
		return SKIP_PATH;
	}
}
