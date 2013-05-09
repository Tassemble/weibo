package com.techq.weibo.workers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.naming.LimitExceededException;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.techq.weibo.workers.meta.PPData;

public class ShareWorker extends Thread {
	Logger LOG = Logger.getLogger(ShareWorker.class);
	static String postURl = "http://service.weibo.com/share/aj_share.php";
	static String refer = "http://service.weibo.com/share/share.php?title="
			+ "&url=&source=bookmark&appkey=appkeyitem&pic=picItem&ralateUid=&retcode=0";
	// http://v.t.sina.com.cn/share/share.php?appkey=2684493555&url=&title=%E7%88%B1%20(2012)&ralateUid=&source=sourceUrl=&content=utf8&pic=http%3a%2f%2fmy.wjl.cn%2ftorrentimg%2fs1dc2298ea2a6ad07933cdc0d33f59db561fa61930.png
	String appkey;
	int limit = 5;
	boolean running = true;
	int sendingTimeInterval = 20;// 20min

	PPCrawlerWorker crawler;
	HttpClient client;
	List<PPData> datas = new ArrayList<PPData>();
	static Map<String, String> appkeys = new HashMap<String, String>();
	Random random = new Random(System.currentTimeMillis());
	static {
		appkeys.put("ipad", "2849184197");
		appkeys.put("iphone", "5786724301");
		appkeys.put("androidpad", "2540340328");
		appkeys.put("android", "android");
		appkeys.put("air", "3434422667");
	}

	
	
	
	

	/**
	 * 
	 * @param sendingTimeInterval set how long to send a weibo
	 * @param crawler ppcrawler
	 * @param client httpclient
	 * @param device ipad, iphone, android?
	 */
	public ShareWorker(int sendingTimeInterval, PPCrawlerWorker crawler,
			HttpClient client, String device) {
		super();
		this.sendingTimeInterval = sendingTimeInterval;
		this.crawler = crawler;
		this.client = client;
		appkey = appkeys.get(device);
		if (appkey == null)
			appkey = appkeys.get("ipad");
	}





	@Override
	public void run() {
		LOG.info("start send weibo...");
		int cnt = 0;
		int tick = 0;

		while (cnt < limit && running) {
			tick++;
			try {
				if (datas.size() == 0) {
					datas = this.crawler.crawlSentences();
				}

				if (datas.size() == 0) {
					LOG.error("no message to sending !!!");
					cnt++;
					TimeUnit.SECONDS.sleep(2);
					continue;
				}
				
				PPData p = datas.remove(0);
				LOG.info("writing weibo :" + p.content);
				if (writeWeibo(client, p.content, p.url) == false) {
					LOG.error("post weibo error");
					cnt++;
				}

				if (tick % 3 == 0 || tick % 5 == 0) {
					TimeUnit.MINUTES.sleep(10 + random.nextInt(30));
				} else if (tick % 7 == 0) {
					TimeUnit.MINUTES.sleep(10 + random.nextInt(60));
				}
				else {
					TimeUnit.MINUTES.sleep(sendingTimeInterval + random.nextInt(10));
				}
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

		if (cnt >= limit) {
			LOG.error("Write weibo worker exit because of failed time reach the limit:"
					+ limit);
		}
	}

	public void shutdown() {
		running = false;
		this.interrupt();
	}

	public boolean writeWeibo(HttpClient client, String content, String imgURL)
			throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost(postURl);
		String refURL = URLEncoder.encode(imgURL, "UTF-8");
		String ref = refer.replace("appkeyitem", appkey).replace("picItem",
				refURL);
		System.out.println("http ref:" + ref);
		post.setHeader("Referer", ref);
		post.setHeader("Host", "service.weibo.com");
		post.setHeader("Content-Type",
				"	application/x-www-form-urlencoded; charset=UTF-8");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0");
		post.setHeader("Accept-Charset", "utf-8;q=0.7,*;q=0.7");

		List<NameValuePair> list = new ArrayList<NameValuePair>();
		list.add(new BasicNameValuePair("appkey", appkey));
		list.add(new BasicNameValuePair("content", content));
		list.add(new BasicNameValuePair("from", "share"));
		list.add(new BasicNameValuePair("refer", ""));
		list.add(new BasicNameValuePair("share_pic", imgURL));
		list.add(new BasicNameValuePair("source", "bookmark"));
		list.add(new BasicNameValuePair("sourceUrl", "bookmark"));
		list.add(new BasicNameValuePair("styleid", "1"));
		list.add(new BasicNameValuePair("url_type", ""));
		post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));

		HttpResponse respon = client.execute(post);
		post.abort();
		LOG.info("result:" + EntityUtils.toString(respon.getEntity()));

		return respon.getStatusLine().getStatusCode() == 200 ? true : false;
	}

}
