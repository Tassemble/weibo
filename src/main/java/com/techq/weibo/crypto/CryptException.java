package com.techq.weibo.crypto;


public class CryptException extends RuntimeException {

	private static final long serialVersionUID = 4466585350770666658L;

	public CryptException() {
		super();
	}

	public CryptException(String message) {
		super(message);
	}

	public CryptException(Throwable cause) {
		super(cause);
	}

	public CryptException(String message, Throwable cause) {
		super(message, cause);
	}

}
