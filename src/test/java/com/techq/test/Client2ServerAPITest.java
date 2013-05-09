package com.techq.test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.techq.weibo.api.Client2ServerAPI;
import com.techq.weibo.api.Utils;
import com.techq.weibo.api.imp.Client2ServerAPIImp;
import com.techq.weibo.api.imp.UtilsImp;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Response;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;
import com.techq.weibo.proxy.ProxyImp;
import com.techq.weibo.proxy.ProxyImpLocal;
import com.techq.weibo.share.Share;

public class Client2ServerAPITest {
	Logger logger = Logger.getLogger(Client2ServerAPITest.class);

	private final Utils utils = new UtilsImp(){
		@Override
		public String httpGet(HttpClient client, String url) throws WeiboException {
			return new ProxyImpLocal().request(url);
		}
	};
	

	
	//@Test
	public void testSubmitTask2ServerLocal() {
		Share.testStart(new UtilsImp(){
			@Override
			public String httpGet(HttpClient client, String url) {
				
				Response r = new Response();
				r.code = Response.FOLLOW_TASK_COMPLETE;
				return Share.utils.response2String(r);
			}
		});
		Client2ServerAPI api = new Client2ServerAPIImp();
		Task t = new Task(1, 2, 5);
		t.setType(Task.TYPE_SERVER_MY_REQUEST_TASK);
		
		api.submitTask2Server(t);
		

		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Share.testEnd();
	}
	
	//@Test
	public void testAskForTasksLocal() throws WeiboException {
		Share.testStart(new UtilsImp() {
			@Override
			public String httpGet(HttpClient client, String url) {
				Response r = new Response();
				
				Task t = new Task(1, 2, 5);
				List<Task> tasks = new ArrayList<Task>();
				t.setType(Task.TYPE_WEIBO_FOLLOW_TASK);
				tasks.add(t);
				Task t1 = new Task(1, 3, 5);
				t1.setType(Task.TYPE_WEIBO_FOLLOW_TASK);
				tasks.add(t1);
				r.code = 200;
				r.tasks = tasks;
				return Share.utils.response2String(r);
			}
		});
		Client2ServerAPI api = new Client2ServerAPIImp();

		List<Task> tasks = api.askForTasks("1");
		
		for (Task task : tasks) {
			System.out.println(task);
		}
		Share.testEnd();
	}
	
	
	
	
	
	
	//@Test
	public void testAddUser() throws WeiboException {
		Share.testStart(utils);
		Client2ServerAPI api = new Client2ServerAPIImp();
		
		User user = new User();
		user.setUserId("23");
		user.setAge(12l);
		user.setArea("hangzhou");
		user.setEmail("luanlexi@1267.com");
		user.setFansNumAddedToday(122l);
		user.setFansNumLimitToday(1111l);
		user.setFansNumWithUsingApp(1321l);
		user.setIds("123" + User.ID_SEPERATOR + "123");
		user.setOnline(true);
		user.setPassword("haha");
		user.setSex(1l);
		user.setWeiboUrl("http://weibo.com/1517171181");
	
		api.addUser2Server(user);
		
		User user2 = new User();
		user2.setUserId("24");
		user2.setAge(12l);
		user2.setArea("hangzhou");
		user2.setEmail("luanlexi@1267.com");
		user2.setFansNumAddedToday(122l);
		user2.setFansNumLimitToday(1111l);
		user2.setFansNumWithUsingApp(1321l);
		user2.setIds("123" + User.ID_SEPERATOR + "123");
		user2.setOnline(true);
		user2.setPassword("haha");
		user2.setSex(1l);
		user2.setWeiboUrl("http://weibo.com/1517171181");

		api.addUser2Server(user2);
		Share.testEnd();
	}
	
	
	//@Test
	public void testSubmitTask2Server() {
		Share.testStart(utils);
		final Client2ServerAPI api = new Client2ServerAPIImp();
		final Task t = new Task(13, 1, 1);
		api.submitTask2Server(t);

		
		try {
			TimeUnit.SECONDS.sleep(10);
			t.setComplete(true);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		try {
			TimeUnit.SECONDS.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		Share.testEnd();
	}
	
	//@Test
	public void testIntegrateSubmitTask2Server() throws WeiboException {
		Share.testStart(utils);
		final Client2ServerAPI api = new Client2ServerAPIImp();
		final Task t = new Task(12, 23, 1);
		new Thread() {
			@Override
			public void run() {
				api.submitTask2Server(t);
			}
		}.start();
//		
		try {
			TimeUnit.SECONDS.sleep(6);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		api.completeTask(new Task(12, 23, 1));
		
		
		try {
			TimeUnit.SECONDS.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Share.testEnd();
	}
	
	
	//@Test
	public void testCompleteTask() throws WeiboException {
		Share.testStart(utils);
		Client2ServerAPI api = new Client2ServerAPIImp();
		Task t = new Task(12, 13, 1);
		api.completeTask(t);
		Share.testEnd();
	}
	
	
	//@Test
	public void testAskForTasks() throws WeiboException {
		Share.testStart(utils);
		Client2ServerAPI api = new Client2ServerAPIImp();

		List<Task> tasks = api.askForTasks("12");
		
		
		for (Task task : tasks) {
			logger.info(task);
		}
		Share.testEnd();
	}

	
	
	//@Test
	public void testHeartBeat() throws WeiboException {
		Share.testStart(utils);
		Client2ServerAPI api = new Client2ServerAPIImp();
		while(true) {
			api.heartBeat("14");
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("user:12 heart beat");
		}
	}
	
	
	//@Test
	public void testGetIDs2FollowMe() throws WeiboException {
		Share.testStart(utils);

		final Client2ServerAPI c2s = new Client2ServerAPIImp();
		User user = new User();
		user.setUserId("2085723430");
		user.setAge(12l);
		user.setArea("hangzhou");
		user.setEmail("luanlexi@1267.com");
		user.setFansNumAddedToday(122l);
		user.setFansNumLimitToday(1111l);
		user.setFansNumWithUsingApp(1321l);
		user.setIds("123" + User.ID_SEPERATOR + "123");
		user.setOnline(true);
		user.setPassword("haha");
		user.setSex(1l);
		user.setWeiboUrl("http://weibo.com/2085723430");
	
		c2s.addUser2Server(user);
		
		List<Task> tasks = c2s.getIDs2FollowMe("2085723430", 0);
		for (Task task : tasks) {
			System.out.println(task);
		}
	}
	
	
	
	public void testTrackTask() throws InterruptedException {
		Share.testStart(utils);
		Utils utils = Share.utils;
		Task task = new Task(12, 1, 3);
		task.setType(Task.TYPE_SERVER_MY_REQUEST_TASK);
		task.setUrl("http://127.0.0.1:8080/rpc?");
		utils.submitTask(task);
		Task track = new Task();
		track.setDelayTime(0);
		track.setFromId(task.getFromId());
		track.setToId(task.getToId());
		track.setType(Task.TYPE_SERVER_CHECK_TASK);
		utils.trackTask(track, task);
		TimeUnit.SECONDS.sleep(10);
		logger.info("set complete true");
		track.isComplete = true;
		TimeUnit.SECONDS.sleep(100);
	}
	
	@Test
	public void testGetUser() throws WeiboException {
		Share.testStart(utils);
		Utils utils = Share.utils;
		final Client2ServerAPI c2s = new Client2ServerAPIImp();
		
		User user2 = c2s.getUser("1772403527");
		logger.info(user2);
	}
	
}
