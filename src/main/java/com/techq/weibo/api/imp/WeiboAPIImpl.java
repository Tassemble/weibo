/**
 * 
 */
package com.techq.weibo.api.imp;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.techq.weibo.api.LogonHandler;
import com.techq.weibo.api.WeiboAPI;
import com.techq.weibo.logic.LogonFactoryManager;

/**
 * @author CHQ
 * @since  2013-2-13
 */
public class WeiboAPIImpl implements WeiboAPI {
	private final static Logger LOG = Logger.getLogger(WeiboAPIImpl.class);

	public static final String SUCCESS = "success";
	public static final String UNKOWN_CODE = "unkonw code";
	
	@Autowired
	HttpClient httpClient;
	
	@Override
	public boolean removeFan(HttpClient client, String uid,  String fid) throws Exception {
		// init
//		if (lastPage == -1) {
//			waitToDelete = finder.find(1);
//			if (waitToDelete.contains(fid)) {
//				waitToDelete.remove(fid);
//			}
//		}

		HttpPost post = new HttpPost("http://weibo.com/aj/f/unfollow?_wv=5&__rnd=" + System.currentTimeMillis());
		post.setHeader("Referer", "	http://weibo.com/" + uid + "/follow?t=1&page=1");

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		// uid=2267500312&f=0&extra=&oid=2085723430&fnick=小炜伊&location=myfollow
		qparams.add(new BasicNameValuePair("refer_sort", "relationManage"));
		qparams.add(new BasicNameValuePair("location", "myfollow"));
		qparams.add(new BasicNameValuePair("refer_flag", "unfollow"));
		qparams.add(new BasicNameValuePair("uid", fid));
		qparams.add(new BasicNameValuePair("_t", "0"));
		UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
		post.setEntity(params);

		HttpResponse response = client.execute(post);

		HttpEntity entity = response.getEntity();
		String page = EntityUtils.toString(entity, "UTF-8");
		post.abort();
		if (isSuccessful(page)) {
			LOG.info("unfollow :" + fid + " successfully");
			return true;
		}
		LOG.info("unfollow :" + fid + " failed!!!");
		return false;
	}
	
	
	private boolean isSuccessful(String page) {
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
	
	
	public String isOK(String page) {
		LOG.debug(page);
		int start = page.indexOf("{\"code\":\"");
		if (start != -1) {
			start += "{\"code\":\"".length();
			int end = page.indexOf("\"", start);
			// System.out.println(page);
			String statusCode = page.substring(start, end);
			if (statusCode.equalsIgnoreCase("100000"))
				return SUCCESS;
			LOG.warn(page);
			return statusCode;
		}
		return UNKOWN_CODE;
	}

	@Override
	public boolean follow(HttpClient innnerClient, long currentUserId, long followId) {
		try {
			
			
			HttpPost post;
			post = new HttpPost("http://weibo.com/aj/f/followed?_wv=5&__rnd="+System.currentTimeMillis());
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("uid", String.valueOf(followId)));
			qparams.add(new BasicNameValuePair("f", "l"));
			qparams.add(new BasicNameValuePair("_t", "0"));
			qparams.add(new BasicNameValuePair("fnick", "努力工作的小湯lC"));
			qparams.add(new BasicNameValuePair("oid", String.valueOf(currentUserId)));
			qparams.add(new BasicNameValuePair("nogroup", "false"));
			qparams.add(new BasicNameValuePair("refer_sort", "followed"));
			qparams.add(new BasicNameValuePair("location", "myfans"));
			UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
			post.setEntity(params);
			post.setHeader("Referer", "http://weibo.com/" + String.valueOf(followId));
			HttpResponse response = innnerClient.execute(post);

			HttpEntity entity = response.getEntity();
			String page = EntityUtils.toString(entity, "UTF-8");

			String result = isOK(page);
			if (result.equals(SUCCESS)) {
				LOG.info("follow is success, reponse code:" + SUCCESS);
				return true;
			}
			LOG.warn("follow is fail, reponse code:" + result);
			post.abort();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}
	
	@Override
	public boolean follow(long currentUserId, long followId) {
		return follow(httpClient, currentUserId, followId);
	}


	@Override
	public boolean logon(HttpClient client, String email, String pwd) throws Exception {
		LogonHandler handler = LogonFactoryManager.getLogonHandler();
		return handler.login(client, email, pwd);
	}


	@Override
	public boolean logon(HttpClient client, String email, String pwd,
			String code) throws Exception {
		LogonHandler handler = LogonFactoryManager.getLogonHandler();
		return handler.login(client, email, pwd, code);
	}

}
