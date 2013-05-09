package com.techq.weibo.crypto;


/**
 * this exception is thrown if a buffer that is meant to have output copied into
 * it turns out to be too short, or if we've been given insufficient input. In
 * general this exception will get thrown rather than an ArrayOutOfBounds
 * exception.
 */
public class DataLengthException extends CryptException {

	private static final long serialVersionUID = 4075358255691884853L;

	public DataLengthException() {
		super();
	}

	public DataLengthException(String message) {
		super(message);
	}

	public DataLengthException(Throwable cause) {
		super(cause);
	}

	public DataLengthException(String message, Throwable cause) {
		super(message, cause);
	}

}
