package com.techq.weibo.meta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.log4j.Logger;

import com.techq.weibo.api.imp.Client2ServerAPIImp;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.share.Share;

public class Task implements Runnable {

	private transient Logger logger = Logger.getLogger(Task.class);
	private transient int delayTime;

	public volatile boolean isComplete;

	private transient Response result;
	public transient static final int TYPE_WEIBO_FOLLOW_TASK = 1;
	public transient static final int TYPE_SERVER_CHECK_TASK = 3;
	public transient static final int TYPE_SERVER_MY_REQUEST_TASK = 4;
	private transient static final String DEFAULT_URL = "http://okhenha.appspot.com/rpc?";
	private transient String url = DEFAULT_URL;
	private int type;

	private long fromId;
	private long toId;

	public synchronized Response getResult() {
		return result;
	}

	public synchronized void setResult(Response result) {
		this.result = result;
	}

	public String getUrl() {
		return url;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public void setDelayTime(int delayTime) {
		this.delayTime = delayTime;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void setComplete(boolean isComplete) {
		this.isComplete = isComplete;
	}

	public Task() {
	}

	public long getFromId() {
		return fromId;
	}

	public void setFromId(long fromId) {
		this.fromId = fromId;
	}

	public long getToId() {
		return toId;
	}

	public void setToId(long toId) {
		this.toId = toId;
	}

	public Task(long fromId, long toId, int delayTime) {
		this.delayTime = delayTime;
		this.fromId = fromId;
		this.toId = toId;
	}

	@Override
	public void run() {
		logger.info("beep");
		if (logger.isInfoEnabled()) {
			if (this.type == Task.TYPE_SERVER_CHECK_TASK) {
				logger.info("track task:" + this.url);
			} else if (this.type == Task.TYPE_SERVER_MY_REQUEST_TASK) {
				logger.info("submit task:" + this.url);
			}
		}
		
		String content;
		try {
			content = Share.utils.httpGet(Client2ServerAPIImp.client2Server, this.url);
		} catch (WeiboException e) {
			logger.error(e);
			return;
		}
		logger.info("ret:" + content);
		switch (this.type) {
		case Task.TYPE_SERVER_MY_REQUEST_TASK:
			logger.info("TYPE_SERVER_MY_REQUEST_TASK:" + content);
			break;
		case Task.TYPE_WEIBO_FOLLOW_TASK:
			logger.info("TYPE_WEIBO_FOLLOW_TASK:" + content);
			break;
			//不断检查是否完成isComplete的线程
		case Task.TYPE_SERVER_CHECK_TASK:
			Response res;
			try {
				res = Share.utils.string2Response(content);
			} catch (WeiboException e) {
				logger.error(e);
				return;
			}
			if (res.code == Response.FOLLOW_TASK_COMPLETE) {
				this.setComplete(true);
				this.setResult(res);
				logger.info("follow_task_complete!:" + this);
			}
			break;
		default:
			logger.error("not defined type:" + this.type);
			break;
		}

	}
	
	

	public Callable<?> newCallable(final Task task) {
		Callable<Boolean> call = new Callable<Boolean>() {
			public Boolean call() {
				if (task.isComplete()) {
					return true;
				}
				return false;
			}
		};
		return call;
	}

	public static Task getNullTask() {
		return new Task(-1, -1, 0);
	}

	
	public String toEncodedUrl() {
		return "fromId=" + fromId + "&isComplete=" + isComplete + "&toId=" + toId + "&type=" + type;
	}

	public static void main(String[] args) {
		new HashMap<String, String>();
		List<Task> tasks = new ArrayList<Task>(Arrays.asList(Task.getNullTask()));
		if (!tasks.contains(Task.getNullTask()))
			tasks.add(Task.getNullTask());
		for (Task task : tasks) {
			System.out.println(task);
		}
		//System.out.println(Share.utils.string2Tasks(Share.utils.tasks2String(tasks)));
		// System.out.println(tasks);
	}
	
	
	public static boolean checkIfNullTask(Task task) {
		return task.getFromId() == -1 && task.getToId() == -1;
	}

	
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (fromId ^ (fromId >>> 32));
		result = prime * result + (int) (toId ^ (toId >>> 32));
		result = prime * result + type;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (fromId != other.fromId)
			return false;
		if (toId != other.toId)
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	
	@Override
	public String toString() {
		return "Task [delayTime=" + delayTime + ", fromId=" + fromId + ", isComplete=" + isComplete + ", toId="
				+ toId + ", type=" + type + "]";
	}



	
	
	
	

}
