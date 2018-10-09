package com.qceda.module.blog.util.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.qceda.module.blog.util.PasswordHelper;

/**
 * Test for BCrypt password encryption
 * 
 * @author vudooman
 *
 */
public class BCryptPasswordHelperImplTest {
	private PasswordHelper helper;

	@Before
	public void setup() {
		this.helper = new BCryptPasswordHelperImpl();
	}

	@Test
	public void testHash() {
		String hash = this.helper.hash("thisismypassword");
		Assert.assertNotNull(hash);
		Assert.assertTrue(hash.length() > 0);
	}

	@Test
	public void testValid() {
		String password = "HandsUp!!UrDed";
		String hash = this.helper.hash(password);
		Assert.assertTrue(this.helper.isValid(password, hash));
	}
}
