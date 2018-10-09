package com.qceda.module.blog.wso;

/**
 * Response object from service call. This allows additional information to be
 * returned to App Controllers when they make calls to various services.
 * 
 * @author vudooman
 *
 * @param <T>
 */
public class ServiceResponse<T> {

	public static enum FailureCode {
		ValidationFailure, InvalidData, Unknown;
	}

	private boolean success;
	private FailureCode failureCode;
	private String failureMessage;
	private String errorReferenceId;
	private T result;

	public ServiceResponse(boolean success) {
		this.success = success;
	}

	public ServiceResponse(boolean success, T result) {
		this(success);
		this.result = result;
	}

	public ServiceResponse(FailureCode failureCode, String failureMessage) {
		this(false, null);
		this.failureCode = failureCode;
		this.failureMessage = failureMessage;
	}

	public ServiceResponse(FailureCode failureCode, String failureMessage, String errorReferenceId) {
		this(failureCode, failureMessage);
		this.errorReferenceId = errorReferenceId;
	}

	public String getErrorReferenceId() {
		return this.errorReferenceId;
	}

	public T getResult() {
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public FailureCode getFailureCode() {
		return failureCode;
	}

	public String getFailureMessage() {
		return failureMessage;
	}
}
