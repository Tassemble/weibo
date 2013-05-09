package com.techq.weibo.meta;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.techq.weibo.crypto.StaticCrypto;

public class Response {
	
	//200
	//1001 id follow complete
	//1002 id follow not complete
	//
	
	
	
	public int code = 0;
	public String msg;
	public List<User> users;// = new ArrayList<User>(Arrays.asList(User.getNullUser()));
	public List<Task> tasks;// = new ArrayList<Task>(Arrays.asList(Task.getNullTask()));
	public int endCode = 0;

	
	
	
	public transient static int FOLLOW_TASK_COMPLETE = 1001;
	public transient static int FOLLOW_TASK_NOT_COMPLETE = 1002;

	@Override
	public String toString() {
		return "Response [code=" + code + ", msg=" + msg + ", tasks=" + tasks
				+ ", users=" + users + ", endCode=" + endCode + "]";
	}



	public static void main(String[] args) {
		Response r = new Response();
		r.code = 200;
		r.msg = "ok";
		User user = new User();
		user.setAge(12);
		user.setArea("hangzhou");
		user.setEmail("chenhongqin@1267.com");
		user.setFansNumAddedToday(122l);
		user.setFansNumLimitToday(1111l);
		user.setFansNumWithUsingApp(1321l);
		user.setIds("123&123");
		user.setOnline(true);
		user.setPassword("haha");
		user.setSex(1);
		user.setUserId("213213");
		user.setWeiboUrl("http://weibo.com/124312341231");

		User user1 = new User();
		user1.setAge(12);
		user1.setArea("hangzhou");
		user1.setEmail("chenhongqin@1267.com");
		user1.setFansNumAddedToday(122);
		user1.setFansNumLimitToday(1111L);
		user1.setFansNumWithUsingApp(1321L);
		user1.setIds("123_123");
		user1.setOnline(true);
		user1.setPassword("haha");
		user1.setSex(1);
		user1.setUserId("213213");
		user1.setWeiboUrl("http://weibo.com/124312341231");

		List<User> lists = Arrays.asList(user, user1);
		r.users = lists;

		Gson json = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Response>() {
		}.getType();
		String toClient = json.toJson(r, type);
		
		System.out.println(toClient);
		Response res = json.fromJson(toClient, type);
		System.out.println(res);
		
		try {
			String enc = StaticCrypto.encrypt(toClient);
			System.out.println(enc);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
