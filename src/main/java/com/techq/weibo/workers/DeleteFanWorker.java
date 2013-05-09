package com.techq.weibo.workers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.techq.weibo.api.WeiboAPI;
import com.techq.weibo.bean.WeiboBeanFactory;
import com.techq.weibo.bo.FollowersFinder;

/**
 * 1. this is worker which find fans with strategy 2. delete fan from my weibo
 * 
 * @author chq
 * 
 */
public class DeleteFanWorker extends Thread {
	private final static Logger LOG = Logger.getLogger(DeleteFanWorker.class);
	int strategy = 1;
	int lastPage = -1;
	int failedTimeLimit = 20;
	// default
	int timeSec = 5;
	String uid;

	HttpClient client;
	
	WeiboAPI weiboAPI;

	
	
	FollowersFinder finder;
	
	
	boolean running = true;
	/**
	 * 
	 * @param client
	 * @param uid
	 *            owner id
	 * @param timeSec
	 *            time to delete the one you follower
	 */
	public DeleteFanWorker(HttpClient client, String uid, int timeSec) {
		init();
		this.client = client;
		this.timeSec = timeSec;
		finder = new FollowersFinder();
		this.uid = uid;
	}
	
	
	private void init() {
		weiboAPI = WeiboBeanFactory.getContext().getBean(WeiboAPI.class);
		finder = WeiboBeanFactory.getContext().getBean(FollowersFinder.class);
	}

	public void run() {
		int cnt = 0;
		List<String> waitToDelete = null;
		while (cnt < failedTimeLimit && running) {
			try {
				if (CollectionUtils.isEmpty(waitToDelete)) {
					waitToDelete = finder.getLastPageFollowers(client, uid);
				}
				if (CollectionUtils.isEmpty(waitToDelete)) {
					LOG.info("there is no followings, turn off delete functions");
					return;
				} else {

				}
				String fid = waitToDelete.remove(0);
				if (!deleteFollowing(uid, fid)) {
					LOG.info("remove fan id:" + fid + "failed!!");
					cnt++;
				} else {
					LOG.info("remove fan id:" + fid);
				}
				TimeUnit.SECONDS.sleep(timeSec);

			} catch (Exception e) {
				cnt++;
				LOG.error(e.getMessage(), e);
			}

		}

		if (cnt >= failedTimeLimit) {
			LOG.warn("delete fans failed cnt is " + cnt
					+ ", it has reached the limit!!, turn off delete functions");
		}

	}
	
	public void shutdown() {
		running = false;
		this.interrupt();
	}

	// {"code":"100000","msg":"","data":{"relation":{"following":0,"follow_me":0}}}
	public boolean deleteFollowing(String uid, String fid) throws Exception {
		return weiboAPI.removeFan(client, uid, fid);
	}

	public boolean isSuccessful(String page) {
		LOG.debug(page);
		int start = page.indexOf("{\"code\":\"");
		if (start != -1) {
			start += "{\"code\":\"".length();
			int end = page.indexOf("\"", start);
			// System.out.println(page);
			String statusCode = page.substring(start, end);
			if (statusCode.equalsIgnoreCase("100000"))
				return true;
		}
		return false;
	}

	// get fans by FanFinderS

	

	public static void main(String[] args) throws IOException {
		List<String> ids = new ArrayList<String>();
		ids.add("1");
		ids.add("2");
		ids.add("3");
		ids.add("4");
		while(!CollectionUtils.isEmpty(ids)) {
			String id = ids.remove(0);
			System.out.println(id);
		}
//		DeleteFanWorker worker = new DeleteFanWorker(null, null, 2);
//		File f = new File(
//				"/home/chq/work/git/works/share-projects/weibo-client/relate/followingPage");
//		BufferedReader is = new BufferedReader(new FileReader(f));
//		String line = is.readLine();
//		StringBuffer buffer = new StringBuffer(1000);
//		do {
//			if (line != null)
//				buffer.append(line);
//			else {
//				break;
//			}
//			line = is.readLine();
//		} while (true);
//		Myfinder finder = worker.new Myfinder();
//		System.out.println("action-data=\\\"uid=3024519967&p");
//		List<String>restList = finder.extractFollowingList("action-data=\\\"uid=3024519967&p");
//		// test
//		System.out.println(restList);

	}

}
