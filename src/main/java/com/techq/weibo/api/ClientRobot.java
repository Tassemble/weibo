package com.techq.weibo.api;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;

public interface ClientRobot {


	
	
	public abstract boolean login(String user, String pwd) throws IOException;

	public abstract String get(String url) throws IOException;

	public abstract boolean follow(long currentUserId, long followId);
	
	public User getUserInfoFromWeibo(String html);
	
	
	public void add2Queue(Task ask);
	
	
	
	/**
	 * enter userId
	 * @param html
	 * @return
	 */
	public boolean checkIfFollowed(String userId) throws WeiboException;
	
	public boolean checkFollowFromWeibo(String html);
	
	
	

}