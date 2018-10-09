package com.qceda.module.blog.wso;

/**
 * Request User Context with auth token is given in request
 * 
 * @author vudooman
 *
 */
public class ReqContextAuth {
	private UserData user;
	private AuthToken token;

	public UserData getUser() {
		return user;
	}

	public void setUser(UserData user) {
		this.user = user;
	}

	public AuthToken getToken() {
		return token;
	}

	public void setToken(AuthToken token) {
		this.token = token;
	}
}
