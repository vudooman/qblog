package com.qceda.module.blog.wso;

import com.qceda.module.blog.entity.User;

/**
 * Request User Data and Data given to client on successful auth
 * 
 * @author vudooman
 *
 */
public class UserData {

	/**
	 * From entity user
	 * 
	 * @see com.qceda.module.blog.entity.User
	 * @param user
	 * @return
	 */
	public static UserData fromUser(User user) {
		UserData userData = new UserData(user.getId());
		userData.setUsername(user.getName());
		userData.setFullName(user.getFullName());
		return userData;
	}

	private Long id;
	private String username;
	private String fullName;

	public UserData(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
}
