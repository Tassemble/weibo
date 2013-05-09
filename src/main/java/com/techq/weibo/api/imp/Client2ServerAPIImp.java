package com.techq.weibo.api.imp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.techq.weibo.WeiboRobot;
import com.techq.weibo.api.Client2ServerAPI;
import com.techq.weibo.api.ClientRobot;
import com.techq.weibo.crypto.StaticCrypto;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Response;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;
import com.techq.weibo.share.Share;

public class Client2ServerAPIImp implements Client2ServerAPI {

	private final static Logger logger = Logger.getLogger(Client2ServerAPIImp.class);
	public final static HttpClient client2Server = new DefaultHttpClient();
	public final static String DEFAULT_HOST = "http://okhenha.appspot.com/rpc?param=";
	public final static String ASK_FOR_TASKS = "op=askfortasks&userId=";
	public final static String COMPLETE_TASK = "op=completeTask&";
	public final static String SUBMIT_TASK_2_SERVER = "op=submitTask&";
	public final static String CHECK_TASK_COMPLETE = "op=checkifcomplete&";
	public final static String GET_IDS_2_FOLLOW_ME = "op=getIds2followme&userId=";
	public final static String ADD_FOLLOW = "op=addfollow&";
	public final static String HEART_BEAT = "op=heartbeat&";
	public final static String ADD_USER = "op=adduser&";
	public final static String PAGE_OFFSET = "&PageOffset=";
	public final static String GET_USER = "op=Getuser&";


	
	private static String getRedirectLocation(String content) {
		System.out.println(content);
		String regex = "location\\.replace\\(\'(.*?)\'\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		String location = null;
		if (matcher.find()) {
			location = matcher.group(1);
		}

		return location;
	}
	
	
	@Override
	public List<Task> askForTasks(String userId) throws WeiboException {
		// http://okhenha.appspot.com/rpc?param=op=askfortasks&userId=12345
		try {
			String url = DEFAULT_HOST + StaticCrypto.encrypt(ASK_FOR_TASKS + userId);
			String content = Share.utils.httpGet(client2Server, url);
			// logger.info("recv:" + content);
			content = filter(content);
			// logger.info("recv:" + content);
			List<Task> tasks = Share.utils.string2Tasks(content);
			return tasks;
		} catch (UnsupportedEncodingException e) {
			logger.error("Client2ServerAPIImp:askForTasks:UnsupportedEncodingException", e);
			e.printStackTrace();
		}
		return Arrays.asList(Task.getNullTask());
	}

	private String filter(String content) throws WeiboException {
		if (content == null || content.length() == 0)
			return "";

		// logger.info(content);
		int start = content.indexOf("{\"code\":");
		int end = content.indexOf("\"endCode\":200}", start) + "\"endCode\":200}".length();
		if (start == -1 || end == -1)
			return "";
		content = content.substring(start, end);
		logger.info(content);
		Response r = Share.utils.string2Response(content);
		try {
			if (r == null)
				return "";
			return Share.utils.tasks2String(r.tasks);
		} catch (NullPointerException e) {
			logger.warn("", e);
		}
		return "";
	}

	private String filterUser(String content) throws WeiboException {
		if (content == null || content.length() == 0)
			return "";

		// logger.info(content);
		int start = content.indexOf("{\"code\":");
		int end = content.indexOf("\"endCode\":200}", start) + "\"endCode\":200}".length();
		if (start == -1 || end == -1)
			return "";
		content = content.substring(start, end);
		logger.info(content);
		return content;

	}

	@Override
	public void completeTask(Task task) throws WeiboException {
		try {
			String url = DEFAULT_HOST + StaticCrypto.encrypt(COMPLETE_TASK + task.toEncodedUrl());
			logger.info("completeTask:" + url);
			String content = Share.utils.httpGet(client2Server, url);
			logger.info(content);
		} catch (UnsupportedEncodingException e) {
			logger.error("Client2ServerAPIImp:completeTask:UnsupportedEncodingException", e);
			e.printStackTrace();
		}
	}

	@Override
	public Task submitTask2Server(Task task) {
		// op=submitTask&fromId=123&toid=1233
		try {
			task.setType(Task.TYPE_SERVER_MY_REQUEST_TASK);
			String url = DEFAULT_HOST + StaticCrypto.encrypt(SUBMIT_TASK_2_SERVER + task.toEncodedUrl());
			task.setUrl(url);
			Share.utils.submitTask(task);

			Task trackTask = new Task(task.getFromId(), task.getToId(), task.getDelayTime());
			trackTask.setType(Task.TYPE_SERVER_CHECK_TASK);
			String url2 = DEFAULT_HOST
					+ StaticCrypto.encrypt(CHECK_TASK_COMPLETE + trackTask.toEncodedUrl());
			trackTask.setUrl(url2);
			Share.utils.trackTask(trackTask, task);
			return trackTask;
		} catch (UnsupportedEncodingException e) {
			logger.error("Client2ServerAPIImp:submitTask2Server:UnsupportedEncodingException", e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Task> getIDs2FollowMe(String userId, int pageOffset) throws WeiboException {
		List<Task> tasks = null;
		try {
			String url = DEFAULT_HOST
					+ StaticCrypto.encrypt(GET_IDS_2_FOLLOW_ME + userId + PAGE_OFFSET + pageOffset);

			String content = Share.utils.httpGet(client2Server, url);
			// logger.info(content);
			content = filter(content);
			tasks = Share.utils.string2Tasks(content);

		} catch (UnsupportedEncodingException e) {
			logger.error("Client2ServerAPIImp:askForTasks:UnsupportedEncodingException", e);
			e.printStackTrace();
		}
		if (tasks == null)
			return Arrays.asList(Task.getNullTask());
		return tasks;
	}

	@Deprecated
	@Override
	public void add2Queue(Task ask) {

	}

	@Override
	public void addUser2Server(User user) throws WeiboException {

		try {
			user.setOnline(true);
			String url = DEFAULT_HOST + StaticCrypto.encrypt(ADD_USER + user.toString());
			String content = Share.utils.httpGet(client2Server, url);
			logger.info(content);
			// content = filter(content);
		} catch (UnsupportedEncodingException e) {
			logger.error("Client2ServerAPIImp:askForTasks:UnsupportedEncodingException", e);
			e.printStackTrace();
		}
	}

	@Deprecated
	@Override
	public boolean checkFollowFromWeibo(String html) {
		return false;
	}

	@Deprecated
	@Override
	public boolean checkIfFollowed(String msg) {
		return false;
	}

	@Deprecated
	@Override
	public boolean follow(long currentUserId, long followId) {
		return false;
	}

	@Deprecated
	@Override
	public String get(String url) throws IOException {
		return "";
	}

	@Deprecated
	@Override
	public User getUserInfoFromWeibo(String html) {
		return null;
	}

	@Deprecated
	@Override
	public boolean login(String user, String pwd) throws IOException {
		logger.error("not support exception");
		return false;
	}

	@Override
	public void addFollow(String fromId, String toId) throws WeiboException {
		try {
			String url = DEFAULT_HOST
					+ StaticCrypto.encrypt(ADD_FOLLOW + "fromId=" + fromId + "&toId=" + toId);
			String content = Share.utils.httpGet(client2Server, url);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void heartBeat(String myId) throws WeiboException {
		try {
			String url = DEFAULT_HOST + StaticCrypto.encrypt(HEART_BEAT + "userId=" + myId);
			Share.utils.httpGet(client2Server, url);
		} catch (UnsupportedEncodingException e) {
			logger.error("", e);
		}
	}

	@Override
	public User getUser(String userId) throws WeiboException {
		try {
			String url = DEFAULT_HOST + StaticCrypto.encrypt(GET_USER + "userId=" + userId);
			String content = Share.utils.httpGet(client2Server, url);
			content = filter(content);
			Response r = Share.utils.string2Response(content);
			if (r != null && r.users == null)
				return r.users.get(0);
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}

}
