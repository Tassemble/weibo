package com.techq.weibo.api;

import org.apache.http.client.HttpClient;

/**
 * 
 * @author CHQ
 * @since  2013-2-13
 */
public interface WeiboAPI {
	public boolean removeFan(HttpClient client, String uid,  String fid) throws Exception;
	
	public boolean logon(HttpClient client, String email, String pwd) throws Exception;

	public boolean logon(HttpClient client, String email, String pwd,
			String code) throws Exception;

	boolean follow(long currentUserId, long followId);

	boolean follow(HttpClient innnerClient, long currentUserId, long followId);
}
