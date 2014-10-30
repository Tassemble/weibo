package com.techq.weibo.meta;


import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.techq.weibo.crypto.StaticCrypto;

/**
 * 
 */

/**
 * @author tassemble@gmail.com 2011-8-2
 */
public class User {
	public static final String KIND = User.class.getSimpleName();
	public static final String USER_IDENTITY = "userId";
	
	private static Logger logger = Logger.getLogger(User.class);
	public static final String ID_SEPERATOR = "_";
	public static final String OTHERS_TASKS = "othersTasks";
	public static final String MY_REQUESTS = "myRequests";
	public static final String FOLLOW_IDS = "follows";
	
	public static final String FANS_Num = "fansNum";
	public static final String FANS_Num_ADDED_TODAY = "fansNumAddedToday";
	String userId;
	String username;
	String password;
	String email;
	String weiboUrl;
	String area;
	long age;
	//default:0:man 1:woman
	long sex;
	boolean isOnline;
	long fansNum;
	long fansNumWithUsingApp;
	String ids;
	long fansNumAddedToday;
	long fansNumLimitToday;
	List<Task> myRequests = Arrays.asList(Task.getNullTask());
	List<Task> othersTasks = Arrays.asList(Task.getNullTask());
	List<String> follows = Arrays.asList("0");
	
	
	public User(String userId, String password) {
		super();
		this.userId = userId;
		this.password = password;
	}

	public User() {
	}

	
	
	
	


	

	public List<Task> getMyRequests() {
		return myRequests;
	}

	public void setMyRequests(List<Task> myRequests) {
		this.myRequests = myRequests;
	}
	
	public void addMyRequests(Task request) {
		synchronized(myRequests) {
			this.myRequests.add(request);
		}
	}
	public void removeMyRequests(Task request) {
		synchronized(myRequests) {
			this.myRequests.remove(request);
		}
	}
	
	
	public List<Task> getOthersTasks() {
		return othersTasks;
	}
	
	public void addOthersTasks(Task task) {
		
		synchronized(othersTasks) {
			this.othersTasks.add(task);
		}
	}
	
	public void removeOthersTasks(Task task) {
		
		synchronized(othersTasks) {
			this.othersTasks.remove(task);
		}
	}

	
	//only start use this
	public void setOthersTasks(List<Task> othersTasks) {
		this.othersTasks = othersTasks;
	}

	public List<String> getFollows() {
		return follows;
	}

	public void setFollows(List<String> follows) {
		this.follows = follows;
	}
	
	public  void addFollow(String follow) {
		synchronized(follows) {
			this.follows.add(follow);
		}
	}

	public long getFansNum() {
		return fansNum;
	}

	public void setFansNum(long fansNum) {
		this.fansNum = fansNum;
	}

	public void setFansNumWithUsingApp(long fansNumWithUsingApp) {
		this.fansNumWithUsingApp = fansNumWithUsingApp;
	}
	
	public long getAge() {
		return age;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public long getSex() {
		return sex;
	}

	public void setSex(long sex) {
		this.sex = sex;
	}

	public long getFansNumWithUsingApp() {
		return fansNumWithUsingApp;
	}

	public long getFansNumAddedToday() {
		return fansNumAddedToday;
	}

	public void setFansNumAddedToday(long fansNumAddedToday) {
		this.fansNumAddedToday = fansNumAddedToday;
	}

	public long getFansNumLimitToday() {
		return fansNumLimitToday;
	}

	public void setFansNumLimitToday(long fansNumLimitToday) {
		this.fansNumLimitToday = fansNumLimitToday;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getWeiboUrl() {
		return weiboUrl;
	}

	public void setWeiboUrl(String weiboUrl) {
		this.weiboUrl = weiboUrl;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}


	

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean isOnline) {
		this.isOnline = isOnline;
	}


	

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}


	public static String getUserIdentity() {
		return USER_IDENTITY;
	}

	@Override
	public String toString() {
		return "age=" + age + "&area=" + area + "&email=" + email
				+ "&fansNumAddedToday=" + fansNumAddedToday
				+ "&fansNumLimitToday=" + fansNumLimitToday
				+ "&fansNumWithUsingApp=" + fansNumWithUsingApp + "&ids="
				+ ids + "&isOnline=" + isOnline + "&password=" + password
				+ "&sex=" + sex + "&userId=" + userId + "&username="
				+ username + "&weiboUrl=" + weiboUrl + "&follows=" + follows
				+ "&othersTasks=" + this.othersTasks + "&myRequests=" + this.myRequests;
	}
	
	

	
	
	public static User getNullUser() {
		return new User();
	}
	
	
	public static void main(String[] args) throws UnsupportedEncodingException {
//		Key k = KeyFactory.createKey(User.class.getSimpleName(), "hisjhdf@12.com");
//		System.out.println(KeyFactory.keyToString(k));responseJSON
//		User.responseJSON();
		encryptUserForTest();
		System.out.println(encry("op=getonlineusers"));
	}

	private static void encryptUserForTest()
			throws UnsupportedEncodingException {
		User user1 = new User();
		user1.setAge(12l);
		user1.setArea("hangzhou");
		user1.setEmail("chenhongqin@1267.com");
		user1.setFansNumAddedToday(122l);
		user1.setFansNumLimitToday(1111l);
		user1.setFansNumWithUsingApp(1321l);
		user1.setIds("123_123");
		user1.setOnline(true);
		user1.setPassword("haha");
		user1.setSex(1);
		user1.setUserId("213213");
		user1.setWeiboUrl("http://weibo.com/124312341231");
		String ret = StaticCrypto.encrypt("op=adduser&" + user1.toString());
		System.out.println("param=" + ret);
	}
	
	
	public static String encry(String s) {
		try {
			return StaticCrypto.encrypt(s);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
