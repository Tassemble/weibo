package com.techq.weibo.bo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.border.EmptyBorder;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

/**
 * 
 * @author CHQ
 * @since 2013-2-13
 */
public class FollowersFinder {
	private final Logger LOG = Logger.getLogger(FollowersFinder.class);

	public List<String> getLastPageFollowers(HttpClient client, String uid) throws ClientProtocolException, IOException {
		int followingNum = getFollowingNum(client, uid);
		int lastPage = getLastPage(followingNum);
		String url = "http://weibo.com/" + uid + "/myfollow?t=1&page=" + lastPage;
		LOG.info("httpget:" + url);
		HttpResponse res = client.execute(new HttpGet(url));
		String html = EntityUtils.toString(res.getEntity());
		// egrep -o "img usercard=\\\\\"id=([0-9]+)\\\\\"" a | egrep -o
		// "[0-9]+"
		List<String> list = extractFollowingList(html);
		return list;
	}

	public List<String> getFollowersFromGroup(HttpClient client, String groupId, String uid) {
		return getFollowersWithCondition(client, uid, "&gid=" + groupId);
		// egrep -o "img usercard=\\\\\"id=([0-9]+)\\\\\"" a | egrep -o
		// "[0-9]+"
	}

	public List<String> getFollowersWithCondition(HttpClient client, String uid, String condition) {
		int i = 1;

		List<String> results = new ArrayList<String>();
		String url = "http://weibo.com/" + uid + "/myfollow?t=1&" + condition;
		while (true) {
			try {
				List<String> follows = getFollowByPage(client, url, i++);
				if (!CollectionUtils.isEmpty(follows)) {
					results.addAll(follows);
				} else {
					break;
				}
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return results;
	}

	
	public List<String>  getFollowByPage(HttpClient client, String url, int page) {
		List<String> results = new ArrayList<String>();
		try {
			String realUrl = url + "&page=" + page;
			LOG.info("req:" + realUrl);
			HttpResponse res = client.execute(new HttpGet(realUrl));
			
			String html = EntityUtils.toString(res.getEntity());
			List<String> list = extractFollowingList(html);

			if (!CollectionUtils.isEmpty(list)) {
				results.addAll(list);
			}
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return results;
	}
	private List<String> extractFollowingList(String html) {
		Pattern p = Pattern.compile("action-data=\\\\\"uid=([0-9]+)");
		Matcher m = p.matcher(html);
		Set<String> list = new HashSet<String>();
		if (m.find()) {
			do {
				LOG.debug("add following:" + m.group(1));
				list.add(m.group(1));
			} while (m.find());
		} else {
			LOG.error("find no followings!!!");
		}

		return new ArrayList<String>(list);
	}

	private int getLastPage(int followingNum) throws ClientProtocolException, IOException {
		return (int) Math.ceil((double) followingNum / 31.0);
	}

	public int getFollowingNum(HttpClient client, String uid) throws ClientProtocolException, IOException {
		String url = "http://weibo.com/" + uid + "/myfollow";
		LOG.info("httpget:" + url);
		HttpResponse res = client.execute(new HttpGet(url));
		String html = EntityUtils.toString(res.getEntity());

		return extractFollowingNum(html);
	}

	private Integer extractFollowingNum(String html) {
		Pattern p = Pattern.compile("全部关注\\(([0-9]+)\\)");
		// \u6211\u5173\u6ce82000\u4eba
		Matcher m = p.matcher(html);
		if (m.find()) {
			// System.out.println(m.group(1));
			LOG.info("follows num:" + m.group(1));
			return Integer.valueOf(m.group(1));
			// return null;
		} else {
			LOG.error("what have you done ? it seems error!");
		}
		return new Integer(-1);
	}

}
