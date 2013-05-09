package com.techq.weibo.api;

import org.apache.http.client.HttpClient;

public interface LogonHandler {
	public Object login(String username, String password);
	
	public boolean login(HttpClient client, String username, String password) throws Exception;
	
	
	public boolean login(HttpClient client, String username, String password, String code)
	
	throws Exception;
	
	

	void afterLogon(HttpClient client, Long uid);
	
	
	
}
