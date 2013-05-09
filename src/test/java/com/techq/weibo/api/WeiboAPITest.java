package com.techq.weibo.api;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import com.techq.test.BaseTestCase;
import com.techq.weibo.WeiboRobot;
import com.techq.weibo.bo.FollowersFinder;
import com.techq.weibo.util.CommonUtils;

public class WeiboAPITest extends BaseTestCase {
	private static final Logger LOG = Logger.getLogger(WeiboAPITest.class);

	@Autowired
	WeiboAPI weiboAPI;

	@Autowired
	FollowersFinder finder;

	@Autowired
	HttpClient httpClient;

	@Autowired
	WeiboRobot robot;

	@Test
	public void testDeleteFollower() throws Exception {
		HttpClient client = new DefaultHttpClient();
		if (weiboAPI.logon(client, "chen-hongqin@163.com", "foreversina")) {
			LOG.info("logon successfully!!!");
			//2485453432  2245145907 2620496505
			weiboAPI.removeFan(client, "1772403527", "2485453432");
			weiboAPI.removeFan(client, "1772403527", "2245145907");
			weiboAPI.removeFan(client, "1772403527", "2620496505");
		} else {
			LOG.info("logon failed!!!");
		}
	}

	@Test
	public void testFindFollowers() throws Exception {
		HttpClient client = new DefaultHttpClient();
		if (weiboAPI.logon(client, "luanlexi@163.com", "test")) {
			LOG.info("logon successfully!!!");
			List<String> list = finder.getLastPageFollowers(client, "2085723430");

		} else {
			LOG.info("logon failed!!!");
		}
	}

	@Test
	public void findByGroup() throws ClientProtocolException, IOException, Exception {
		if (weiboAPI.logon(httpClient, "chen-hongqin@163.com", "foreversina")) {
			LOG.info("logon successfully!!!");
			List<String> remaininglist = finder.getFollowersFromGroup(httpClient, "201111270615662481", "1772403527");

			CommonUtils.printJson(remaininglist);
		}
	}

	@Test
	public void findByCondition() throws ClientProtocolException, IOException, Exception {
		if (weiboAPI.logon(httpClient, "chen-hongqin@163.com", "foreversina")) {
			LOG.info("logon successfully!!!");
			List<String> remaininglist = finder.getFollowersWithCondition(httpClient, "1772403527", "ignoreg=1");

			CommonUtils.printJson(remaininglist.size());
		}
	}

	@Test
	public void deleteByCondition() throws ClientProtocolException, IOException, Exception {
		
		HttpClient httpClient = new DefaultHttpClient();
		if (weiboAPI.logon(httpClient, "chen-hongqin@163.com", "foreversina")) {
			LOG.info("logon successfully!!!");
			List<String> remaininglist = finder.getFollowersFromGroup(httpClient, "201111270615662481", "1772403527");

			String url = "http://weibo.com/1772403527/myfollow?t=1&ignoreg=1";
			for (int index = 4; ;) {
				List<String> deletinglist = finder.getFollowByPage(httpClient, url, index);
				if (CollectionUtils.isEmpty(deletinglist)) {
					if (index >= 6) {
						break;
					}
					index++;
					continue;
				}
				for (String id : deletinglist) {
					if (!remaininglist.contains(id)) {
						LOG.info("remove id:" + id);
						TimeUnit.MILLISECONDS.sleep(30);
						weiboAPI.removeFan(httpClient, "1772403527", id);

					}
				}
			}
		} else {
			LOG.info("logon failed!!!");
		}
	}

	@Test
	public void testFollow() throws Exception {
		if (weiboAPI.logon(httpClient, "luanlexi@163.com", "chen1234")) {
			System.out.println(weiboAPI.follow(httpClient, 2085723430L, 3179972917L));
		} else {
			LOG.info("logon failed!!!");
		}
	}

	@Test
	public void print() {
		System.out.println("\u62b1\u6b49\uff0c\u5173\u6ce8\u5931\u8d25(>_<) \uff0c\u7a0d\u540e\u518d\u8bd5\u5566\u3002");
	}

}
