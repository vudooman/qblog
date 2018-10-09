package com.qceda.module.blog.util;

/**
 * Password encryption at rest strategy
 * 
 * @author vudooman
 *
 */
public interface PasswordHelper {

	String hash(String password);

	boolean isValid(String password, String hash);
}
