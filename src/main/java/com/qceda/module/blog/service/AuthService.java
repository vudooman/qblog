package com.qceda.module.blog.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.gson.Gson;
import com.qceda.module.blog.AppProps;
import com.qceda.module.blog.entity.Credential;
import com.qceda.module.blog.entity.CredentialType;
import com.qceda.module.blog.entity.User;
import com.qceda.module.blog.repo.UserRepo;
import com.qceda.module.blog.util.LogHelper;
import com.qceda.module.blog.util.PasswordHelper;
import com.qceda.module.blog.wso.AuthToken;
import com.qceda.module.blog.wso.ReqContextAuth;
import com.qceda.module.blog.wso.ServiceResponse;
import com.qceda.module.blog.wso.UserAuthToken;
import com.qceda.module.blog.wso.UserData;

/**
 * This service class handles authentication and authorization for the app
 * 
 * @author vudooman
 *
 */
@Service
public class AuthService {

	public static final String USER_EXISTS = "USERNAME_REQUIRED";
	public static final String USERNAME_REQUIRED = "USERNAME_REQUIRED";
	public static final String PASSWORD_REQUIRED = "PASSWORD_REQUIRED";

	private static Logger logger = LoggerFactory.getLogger(AuthService.class);

	private static final int TOKEN_CHECK_INTERVAL = 30000;

	private static final String USER_CLAIM = "user";

	private static final Gson gson = new Gson();

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordHelper passwordHelper;

	/**
	 * Verify that a bearer token is valid. If if is valid and it is older than the
	 * TOKEN_CHECK_INTERNVAL time (e.g. 5 minutes), a new token will be created and
	 * returned. Client should then used the updated token if it wants session
	 * prolongation due to user's activity. This allows for stateless session
	 * management.
	 * 
	 * @param bearerToken - bearer token
	 * @return
	 */
	public ServiceResponse<ReqContextAuth> verifyAuth(String bearerToken) {
		ServiceResponse<ReqContextAuth> res = null;
		ReqContextAuth reqCtx = new ReqContextAuth();
		JWTVerifier verifier = JWT.require(this.getTokenAlgorithm()).withIssuer("auth0").build();
		DecodedJWT jwt = null;
		try {
			jwt = verifier.verify(bearerToken);
			String userJson = jwt.getClaim(USER_CLAIM).asString();
			UserData user = gson.fromJson(userJson, UserData.class);
			reqCtx.setUser(user);
			long elapsedTime = System.currentTimeMillis() - jwt.getIssuedAt().getTime();
			if (elapsedTime > TOKEN_CHECK_INTERVAL) {
				String token = this.generateTokenFromUser(user);
				reqCtx.setToken(this.createAuthToken(token));
			}
			res = new ServiceResponse<>(true, reqCtx);
		} catch (JWTDecodeException ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		} catch (TokenExpiredException | SignatureVerificationException ex) {
			String refId = LogHelper.debug(logger, String.format("Invalid bearer token: %s", bearerToken), ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.InvalidData, null, refId);
		}

		return res;
	}

	/**
	 * Verify if username/password is valid for login. If it is create bearer token
	 * and returned with user info.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional(readOnly = true)
	public UserAuthToken getAuthToken(String username, String password) {
		String token = null;
		User user = this.userRepo.findUserByUsernameOrEmail(username);
		if (user != null && user.getCredentials() != null && !user.getCredentials().isEmpty()) {

			for (Credential uCred : user.getCredentials()) {
				CredentialType type = CredentialType.valueOf(uCred.getType());
				if (type == CredentialType.BASIC) {
					if (passwordHelper.isValid(password, uCred.getValue())) {
						token = this.generateTokenFromUser(UserData.fromUser(user));
					}
					break;
				}
			}

		}
		AuthToken authToken = token != null && token.length() > 0 ? this.createAuthToken(token) : null;

		UserData userData = user == null ? null : UserData.fromUser(user);
		return new UserAuthToken(userData, authToken);
	}

	/**
	 * Create a new user account by username and password. If username already
	 * exist, method will return false for not creating new user.
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Transactional
	public ServiceResponse<User> registerAccount(String username, String password) {
		if (username == null || username.length() == 0) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure, USERNAME_REQUIRED);
		} else if (password == null || password.length() == 0) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure, PASSWORD_REQUIRED);
		}

		User user = this.userRepo.findUserByUsernameOrEmail(username);
		if (user == null) {
			user = new User();
			user.setName(username);

			Credential cred = new Credential();
			cred.setType(CredentialType.BASIC.name());
			cred.setValue(passwordHelper.hash(password));

			List<Credential> uCred = new ArrayList<>(1);
			uCred.add(cred);
			user.setCredentials(uCred);

			user = this.userRepo.save(user);
			if (user == null || user.getId() == null) {
				// Could not create user
				return new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null);
			}

			// User created
			return new ServiceResponse<>(true, user);
		}

		// User account already exists
		return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure, USER_EXISTS);
	}

	protected AuthToken createAuthToken(String token) {
		return new AuthToken("jwt", token);
	}

	/**
	 * Create new JWT token with specific expiration date based on configuration
	 * data provided by app props. Also save issued date and user info in token for
	 * stateless session management.
	 * 
	 * @param user
	 * @return
	 */
	protected String generateTokenFromUser(UserData user) {
		return JWT.create().withIssuer("auth0").withExpiresAt(this.getTokenExpireDate()).withIssuedAt(new Date())
				.withClaim(USER_CLAIM, gson.toJson(user)).sign(this.getTokenAlgorithm());
	}

	/**
	 * Specify token generation algorithm.
	 * 
	 * @return
	 */
	protected Algorithm getTokenAlgorithm() {
		// Simple for now, we can registered with an auth provider for
		// public/private key for a more secure algorithm later.
		return Algorithm.HMAC256(AppProps.getInstance().getProperty(AppProps.JWT_SECRET));
	}

	/**
	 * Token lifetime based on app props
	 * 
	 * @return
	 */
	protected Date getTokenExpireDate() {
		return new Date(System.currentTimeMillis()
				+ Long.parseLong(AppProps.getInstance().getProperty(AppProps.TOKEN_TTL)) * 60 * 1000
				+ TOKEN_CHECK_INTERVAL);
	}
}
