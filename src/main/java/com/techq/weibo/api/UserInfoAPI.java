package com.techq.weibo.api;

public interface UserInfoAPI {

	public String getUserId(String html);
	
	
	public String getUsername(String html);
	
	
	public String getUserUrl(String html);
	
	
	
	public String getUserArea(String html);
	
	
	public long getUserAge(String html);
	
	
	public long getUserSex(String html);
	
	
	public long getFansNum(String html);
	
}
