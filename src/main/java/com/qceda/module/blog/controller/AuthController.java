package com.qceda.module.blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qceda.module.blog.entity.User;
import com.qceda.module.blog.service.AuthService;
import com.qceda.module.blog.util.RequestHelper;
import com.qceda.module.blog.wso.ServiceResponse;
import com.qceda.module.blog.wso.UserAuthToken;
import com.qceda.module.blog.wso.UsernamePassword;

/**
 * Controller to handle all authentication requests
 * 
 * @author vtran
 *
 */
@RestController
@RequestMapping(value = "/api")
public class AuthController {

	@Autowired
	private AuthService authService;

	/**
	 * Handle login using username password
	 * 
	 * @param credential
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<UserAuthToken> passordLogin(@RequestBody UsernamePassword credential) {
		UserAuthToken token = this.authService.getAuthToken(credential.getUsername(), credential.getPassword());
		if (token != null && token.getAuthToken() != null) {
			return new ResponseEntity<>(token, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

	}

	/**
	 * Handle user account registration with username and password.
	 * 
	 * @param credential
	 * @return
	 */
	@PostMapping("/account")
	public ResponseEntity<?> createAccount(@RequestBody UsernamePassword credential) {
		ServiceResponse<User> res = this.authService.registerAccount(credential.getUsername(),
				credential.getPassword());
		if (res.isSuccess()) {
			return this.passordLogin(credential);
		}
		return RequestHelper.handleServiceFailure(res);

	}
}