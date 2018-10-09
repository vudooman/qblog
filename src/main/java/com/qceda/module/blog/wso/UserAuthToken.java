package com.qceda.module.blog.wso;

/**
 * Information returned on successful login to client
 * 
 * @author vudooman
 *
 */
public class UserAuthToken {
	private UserData user;
	private AuthToken authToken;

	public UserAuthToken(UserData user, AuthToken authToken) {
		this.user = user;
		this.authToken = authToken;
	}

	public UserData getUser() {
		return this.user;
	}

	public AuthToken getAuthToken() {
		return this.authToken;
	}
}
