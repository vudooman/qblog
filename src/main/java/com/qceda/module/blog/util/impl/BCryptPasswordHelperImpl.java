package com.qceda.module.blog.util.impl;

import org.mindrot.jbcrypt.BCrypt;

import com.qceda.module.blog.util.PasswordHelper;

/**
 * Password at rest strategy using BCrypt
 * 
 * @author vudooman
 *
 */
public class BCryptPasswordHelperImpl implements PasswordHelper {

	private static final int ROUNDS = 10;

	@Override
	public String hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt(ROUNDS));
	}

	@Override
	public boolean isValid(String password, String hash) {
		return BCrypt.checkpw(password, hash);
	}

}
