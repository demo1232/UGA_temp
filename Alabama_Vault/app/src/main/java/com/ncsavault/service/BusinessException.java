package com.ncsavault.service;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private String data;

	public BusinessException(String errorCode) {
		super("Error code: " + errorCode);
		this.errorCode = errorCode;
	}

	public BusinessException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
	}

	@SuppressWarnings("unused")
	public void setData(String data) {
		this.data = data;
	}

	@SuppressWarnings("unused")
	public String getData() {
		return data;
	}

	@SuppressWarnings("unused")
	public String getErrorCode() {
		return errorCode;
	}
}
