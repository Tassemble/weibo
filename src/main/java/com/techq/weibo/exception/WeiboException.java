package com.techq.weibo.exception;

public class WeiboException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public WeiboException(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	private int code;
	private String msg;
	
	
	public final static int INTERNAL_ERROR = 1100;
	public final static int URL_NULL_ERROR = 1101;
	public final static int STRING_2_RESPONSE_ERROR = 1102;

}
