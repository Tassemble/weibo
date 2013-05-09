package com.techq.weibo.api.imp;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.techq.weibo.api.Client2ServerAPI;
import com.techq.weibo.api.Utils;
import com.techq.weibo.dao.DAOLocker;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Response;
import com.techq.weibo.meta.Task;
import com.techq.weibo.proxy.Proxy;
import com.techq.weibo.proxy.ProxyImp;
import com.techq.weibo.proxy.ProxyImpLocal;
import com.techq.weibo.share.ClientConfig;
import com.techq.weibo.share.ClientHeartBeat;
import com.techq.weibo.share.ProducerComsumer;
import com.techq.weibo.share.Share;

public class UtilsImp implements Utils {
	
	private final Logger logger = Logger.getLogger(Client2ServerAPIImp.class);
	public final DAOLocker locker = new DAOLocker();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(50);
	private final Random r = new Random();
	public final ExecutorService producerComsumerService = Executors.newCachedThreadPool();
	private final ProducerComsumer<Task> followTasks = new ProducerComsumer<Task>(new LinkedBlockingQueue<Task>());
	private final Proxy proxy = new ProxyImp();
	public static volatile boolean isShutDown = false;
	
	
	@Override
	public String response2String(Response res) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Response>() {
		}.getType();
		return gson.toJson(res, type);
		
	}

	@Override
	public List<Task> string2Tasks(String str) {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<List<Task>>() {
		}.getType();
		return gson.fromJson(str, type);
	}

	@Override
	public String tasks2String(List<Task> tasks) {
		if (tasks == null)
			return "";
		Gson gson = new Gson();
		return gson.toJson(tasks);
	}

	@Override
	public String httpGet(HttpClient client, String url) throws WeiboException {
		return new ProxyImp().request(url);
		//return doHttpGet(client, url);
	}
	
	
	

	private String doHttpGet(HttpClient client, String url) {
		logger.info("Http get:" + url);
		String result;
		try {
			HttpGet get = new HttpGet(url);
			HttpResponse response = client.execute(get);

			System.out.println(response.getStatusLine());
			HttpEntity entity = response.getEntity();

			result = EntityUtils.toString(entity, "UTF-8");
			get.abort();
			return result;
		} catch (ClientProtocolException e) {
			logger.error("httpGet:ClientProtocolException", e);
			e.printStackTrace();
		} catch (ParseException e) {
			logger.error("ParseException", e);
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("ParseException", e);
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public ScheduledFuture<?> trackTask(final Task task, final Task trackedTask) {
		
		// task to track
		logger.info("track task: " + task);
		final ScheduledFuture<?> trackHandle = scheduler.scheduleAtFixedRate(task, task.getDelayTime(), 
				ClientConfig.TASK_TRACK_PERIOD, TimeUnit.SECONDS);

		// cancel track
		scheduler.schedule(new Runnable() {
			
			public void run() {
				int t = ClientConfig.CANCLE_TRACK_TIME;
				while (!task.isComplete && t > 0) {
					//logger.info("isComplete:" + task.isComplete);
					try {
						t--;
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						logger.equals(e);
						e.printStackTrace();
					}
					logger.debug("task is not completed: " + trackedTask);
				}
				if (t <= 0) {
					logger.warn("task is cancled:" + trackedTask);
					trackHandle.cancel(true);	
				} else {
					logger.info("task is completed: " + trackedTask);
					trackHandle.cancel(true);
				}
			}
		}, 1, TimeUnit.SECONDS);
		return trackHandle;
	}

	
	public ScheduledFuture<?> trackTask1(final Task trackedTask) {

		Task task = new Task();
		task.setDelayTime(0);
		task.setFromId(trackedTask.getFromId());
		task.setToId(trackedTask.getToId());
		task.setType(Task.TYPE_SERVER_CHECK_TASK);
		
		
		final ScheduledFuture<?> trackHandle = scheduler.scheduleAtFixedRate(task, task.getDelayTime(), 
				ClientConfig.TASK_TRACK_PERIOD, TimeUnit.SECONDS);

		// cancel track
		scheduler.schedule(new Runnable() {
			
			public void run() {
				int t = ClientConfig.CANCLE_TRACK_TIME;
				while (!trackedTask.isComplete() && t > 0) {
					try {
						t--;
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						logger.equals(e);
						e.printStackTrace();
					}
					logger.debug("task is not completed: " + trackedTask);
				}
				if (t <= 0) {
					logger.warn("task is cancled:" + trackedTask);
					trackHandle.cancel(true);	
				} else {
					logger.info("task is completed: " + trackedTask);
					trackHandle.cancel(true);
				}
			}
		}, 1, TimeUnit.SECONDS);
		task = null;
		return trackHandle;
	}
	
	
	@Override
	public ScheduledFuture<?> submitTask(Task task) {
		logger.info("submit task: " + task);

		final ScheduledFuture<?> submitHandle = scheduler.schedule(task, task.getDelayTime(), TimeUnit.SECONDS);
		return submitHandle;
	}

	@Override
	public void addFollowTask(Task task) {
		if (!followTasks.isStarted()) {
			followTasks.start();
		}
		followTasks.add(task);
	}

	@Override
	public Response string2Response(String str) throws WeiboException {
		Gson gson = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Response>() {
		}.getType();
		Response r;
		try {
			r = gson.fromJson(str, type);
		} catch (JsonSyntaxException e) {
			throw new WeiboException(WeiboException.STRING_2_RESPONSE_ERROR, "", e);
		}
		return r;
	}

	@Override
	public DAOLocker getLock() {
		return this.locker;
	}

	@Override
	public void addProduceComsumer(ProducerComsumer<Task> pc) {
		producerComsumerService.execute(pc);
	}

	/**
	 * only client use this method
	 */
	@Override
	public void startClientHeartBeat(final Client2ServerAPI api, final String userId) {
		Runnable heartBeat = new Runnable() {
			public void run() {
				try {
					api.heartBeat(userId);
				} catch (WeiboException e) {
					logger.error("", e);
				}
			}
		};
		scheduler.scheduleAtFixedRate(heartBeat, 0, ClientConfig.HEART_BEAT_TIME, TimeUnit.SECONDS);
	}

	
	@Override
	public String map2String(Map<String, Long> map) {
		Gson son = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Long>>() {
		}.getType();
		return son.toJson(map, type);
	}
	
	@Override
	public Map<String, Long> string2Map(String str) {
		Gson son = new Gson();
		java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Long>>() {
		}.getType();
		return son.fromJson(str, type);
	}

//	public static void main(String[] args) {
//		Response r = new Response();
//		UtilsImp i = new UtilsImp();
//		System.out.println(i.response2String(r));
//	}

	@Override
	public boolean isValid(long fromId, long toId) {
		if (fromId == -1 || toId == -1)
			return false;
		
		return true;
	}
	
	
	public static void main(String[] args) throws WeiboException {
		UtilsImp utils = new UtilsImp();
		Response r = utils.string2Response("{\"code\":805,\"msg\":\"\"}");
		
		
		if (r == null)
			System.out.println("null");
		else 
			System.out.println(r);
		utils.tasks2String(r.tasks);
	}

	@Override
	public void shutdownService() {
		isShutDown = true;
		scheduler.shutdownNow();
		producerComsumerService.shutdownNow();
	}
	
	@Override
	public boolean isShutDown() {
		return isShutDown;
	}
}
