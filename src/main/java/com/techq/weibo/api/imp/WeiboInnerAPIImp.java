package com.techq.weibo.api.imp;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.techq.weibo.WeiboRobot;
import com.techq.weibo.api.ClientRobot;
import com.techq.weibo.api.UserInfoAPI;
import com.techq.weibo.api.WeiboInnerAPI;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;
import com.techq.weibo.share.Share;

public class WeiboInnerAPIImp implements WeiboInnerAPI {

	private final static Logger logger = Logger.getLogger(Client2ServerAPIImp.class);
	public final static HttpClient weiboClient = new DefaultHttpClient();
	ClientRobot robot = new WeiboRobot();
	
	
	@Override
	public boolean follow(long currentId, long followId) {
		return robot.follow(currentId, followId);
	}

	@Override
	public String getIndexHTML(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasFollowed(long currentId, long followId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Deprecated
	@Override
	public void add2Queue(Task ask) {
		
	}


	
	@Override
	public boolean checkFollowFromWeibo(String html) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean checkIfFollowed(String userId) throws WeiboException {
		return robot.checkIfFollowed(userId);
	}

	@Override
	public String get(String url) throws IOException {
		return robot.get(url);
	}

	@Override
	public User getUserInfoFromWeibo(String html) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean login(String email, String pwd) throws IOException {
		return robot.login(email, pwd);
	}

	
	@Override
	public User getUser(String userId) {
		User user = new User();
		UserInfoAPI api  = new UserInfoAPIImp();
		String content = null;
		try {
			content = robot.get("http://weibo.com/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setUserId(api.getUserId(content));
		user.setFansNum(api.getFansNum(content));
		user.setUsername(api.getUsername(content));
		//
		
		
		return user;
	}
	
	@Override
	public User getCurrentUser() {
		User user = new User();
		UserInfoAPI api  = new UserInfoAPIImp();
		String content = null;
		try {
			content = robot.get("http://weibo.com/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setUserId(api.getUserId(content));
		user.setFansNum(api.getFansNum(content));
		user.setUsername(api.getUsername(content));
		return user;
	}
	
	@Override
	public boolean hasSuchFan(long myId, long fanId) {
		String content = null;
		try {
			
			content = robot.get("http://weibo.com/" + String.valueOf(myId) + "/fans");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (content.contains(String.valueOf(fanId))) {
			return true;
		}
		return false;
	}
}
