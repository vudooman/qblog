package com.qceda.module.blog.wso;

/**
 * Auth token with type information (e.g. jwt)
 * 
 * @author vudooman
 *
 */
public class AuthToken {

	private String type;
	private String token;

	public AuthToken(String type, String token) {
		this.type = type;
		this.token = token;
	}

	public String getType() {
		return type;
	}

	public String getToken() {
		return token;
	}

}
