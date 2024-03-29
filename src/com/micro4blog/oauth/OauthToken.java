package com.micro4blog.oauth;

import javax.crypto.spec.SecretKeySpec;

public class OauthToken {
	
	private String tokenRefresh;
	private long expiresIn;
	private String oauthVerifier;
	private String oauthTokenSecret;
	private String oauthToken;
	private String[] responseStr;
	
	protected SecretKeySpec secretKeySpec;
	
	public OauthToken() {		
	}
	
	public OauthToken(String resultStr) {
		responseStr = resultStr.split("&");
		oauthTokenSecret = getParameter("oauth_token_secret");
		oauthToken = getParameter("oauth_token");
	}
	
	public OauthToken(String token, String secret) {
		oauthToken = token;
		oauthTokenSecret = secret;
	}
	
	private String getParameter(String params) {
		String value = null;
		
		for (String str : responseStr) {
			if (str.startsWith(params + "=")) {
				value = str.split("=")[1].trim();
			}
		}
		
		return value;
	}

	public String getOauthToken() {
		return oauthToken;
	}

	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}

	public String getTokenRefresh() {
		return tokenRefresh;
	}

	public void setTokenRefresh(String tokenRefresh) {
		this.tokenRefresh = tokenRefresh;
	}

	public long getExpiresIn() {
		return expiresIn;
	}

	// 问题在于对expires_in的设定，使得expires_in与服务器返回的不一致，正确的
	
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
	
	public void setExpiresIn(String expiresStr) {
		if (expiresStr != null && ! expiresStr.equals("0")) {
			setExpiresIn(System.currentTimeMillis() + Integer.valueOf(expiresStr) * 1000);
		}
	}

	public String getOauthVerifier() {
		return oauthVerifier;
	}

	public void setOauthVerifier(String oauthVerifier) {
		this.oauthVerifier = oauthVerifier;
	}

	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}

	public void setOauthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}

	public String[] getResponseStr() {
		return responseStr;
	}

	public void setResponseStr(String[] responseStr) {
		this.responseStr = responseStr;
	}

	public SecretKeySpec getSecretKeySpec() {
		return secretKeySpec;
	}

	public void setSecretKeySpec(SecretKeySpec secretKeySpec) {
		this.secretKeySpec = secretKeySpec;
	}
	
	
	

}
