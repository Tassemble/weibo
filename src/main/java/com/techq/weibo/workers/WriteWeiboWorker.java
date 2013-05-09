package com.techq.weibo.workers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.techq.weibo.workers.meta.PPData;

public class WriteWeiboWorker extends Thread {
	Logger LOG = Logger.getLogger(WriteWeiboWorker.class);
	
	int retryLimit = 5;
	HttpClient client;
	String uid;
	PPCrawlerWorker crawler;
	List<PPData> datas = new ArrayList<PPData>();
	/**
	 * 10 min
	 */
	int sendingTimeInterval = 10;


	public WriteWeiboWorker(HttpClient client, String uid, PPCrawlerWorker crawler) {
		super("WriteWeiboWorker");
		this.client = client;
		this.uid = uid;
		this.crawler = crawler;
	}
	
	


	public WriteWeiboWorker(HttpClient client) {
		super("WriteWeiboWorker");
		this.client = client;
	}




	public boolean WriteWeibo(PPData ppData, String uid) throws ClientProtocolException, IOException {
		//http://weibo.com/aj/mblog/add?__rnd=
		//1330874764196
		//1330877085709
		//http://weibo.com/aj/mblog/add?__rnd=1330878760262
		//http://weibo.com/aj/mblog/add?__rnd=1330878567541
		//
		String rnd = String.valueOf(System.currentTimeMillis());
		HttpPost post = makeHeader(rnd, uid);
		post.setEntity(makePostDetail(ppData));
		LOG.info("http post:" + post.getURI());
		HttpResponse response = client.execute(post);
		LOG.info(EntityUtils.toString(response.getEntity()));
		if (response.getStatusLine().getStatusCode() == 200) {
			return true;
		}
		LOG.info("response code:" +response.getStatusLine().getStatusCode());
		return false;
	}


	private HttpPost makeHeader(String rnd, String uid) {
		HttpPost post = new HttpPost("http://weibo.com/aj/mblog/add?__rnd=" + rnd);
		post.setHeader("Refer", "http://weibo.com/u/" +uid);
		post.setHeader("Host", "weibo.com");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("Origin","http://weibo.com");
		post.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		post.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");
		post.setHeader("Accept-Encoding", "gzip,deflate");
		post.setHeader("Accept-Language", "en-US,en;q=0.5");
		post.setHeader("Content-Type", "	application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("Pragma", "no-cache");
//	kv.add(new BasicNameValuePair("Pragma", "no-cache"));

		return post;
	}
	
	
	private HttpEntity makePostDetail(PPData ppData) throws UnsupportedEncodingException {

		List<NameValuePair> kv = new ArrayList<NameValuePair>();
		kv.add(new BasicNameValuePair("_surl", ""));
		kv.add(new BasicNameValuePair("_t", "0"));
		kv.add(new BasicNameValuePair("location", "home"));
		kv.add(new BasicNameValuePair("rank", ""));
		kv.add(new BasicNameValuePair("module", "stissue"));
		kv.add(new BasicNameValuePair("pic_id", ""));
		kv.add(new BasicNameValuePair("text", ppData.content));
	
		return new UrlEncodedFormEntity(kv);
	}


	@Override
	public void run() {
		int cnt = 0;
		while(cnt < retryLimit) {
			try {
				if (datas.size() == 0) {
					datas = this.crawler.crawlSentences();
				}
				
				if (datas.size() == 0) {
					LOG.error("no message to sending !!!");
				}
				if (this.WriteWeibo(datas.remove(0), uid) == false) {
					cnt++;
				}
				
				
				TimeUnit.MINUTES.sleep(sendingTimeInterval);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cnt++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cnt++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				cnt++;
			}
		}
		
		if (cnt >=  retryLimit) {
			LOG.error("Write weibo worker exit because of failed time reach the limit:" + retryLimit);
		}
		
	}
	
}
