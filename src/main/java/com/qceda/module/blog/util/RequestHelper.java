package com.qceda.module.blog.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.qceda.module.blog.filter.AuthFilter;
import com.qceda.module.blog.wso.ReqContextAuth;
import com.qceda.module.blog.wso.ServiceResponse;
import com.qceda.module.blog.wso.UserData;

/**
 * Utility helper to handle requests common activities such as authentication
 * required or service failure
 * 
 * @author vudooman
 *
 */
public class RequestHelper {

	public static final String TOKEN_INVALID = "TOKEN_INVALID";
	private static final String TOKEN_REQUIRED = "TOKEN_REQUIRED";

	/**
	 * Auth handler when auth info is available
	 * 
	 * @author vudooman
	 *
	 */
	public static interface AuthRequiredHandler {
		ResponseEntity<?> handleRequest(UserData user);
	}

	/**
	 * Template for auth required request. Will call handler if proper
	 * authentication is available.
	 * 
	 * @param handler
	 * @return
	 */
	public static ResponseEntity<?> authRequiredRequest(AuthRequiredHandler handler) {
		ResponseEntity<?> res = null;
		ServiceResponse<ReqContextAuth> authRes = AuthFilter.getAuthResponse();
		if (authRes == null) {
			res = new ResponseEntity<>(
					new ServiceResponse<>(ServiceResponse.FailureCode.InvalidData, TOKEN_REQUIRED, null),
					HttpStatus.UNAUTHORIZED);
		} else if (!authRes.isSuccess()) {
			String failureMessage = null;
			if (ServiceResponse.FailureCode.InvalidData == authRes.getFailureCode()) {
				failureMessage = TOKEN_INVALID;
			}
			res = new ResponseEntity<>(
					new ServiceResponse<>(authRes.getFailureCode(), failureMessage, authRes.getErrorReferenceId()),
					HttpStatus.UNAUTHORIZED);
		} else {
			res = handler.handleRequest(authRes.getResult().getUser());
		}
		return res;
	}

	/**
	 * Utility method to handle service failure
	 * 
	 * @param res
	 * @return
	 */
	public static ResponseEntity<?> handleServiceFailure(ServiceResponse<?> res) {
		HttpStatus status = null;
		if (res.getFailureCode() == ServiceResponse.FailureCode.ValidationFailure
				|| res.getFailureCode() == ServiceResponse.FailureCode.InvalidData) {
			status = HttpStatus.BAD_REQUEST;
		} else {
			status = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<>(res, status);
	}

	// Helper class should have private constructor if not meant to be
	// instantiated
	private RequestHelper() {

	}
}
