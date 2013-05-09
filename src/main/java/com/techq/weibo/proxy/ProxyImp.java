package com.techq.weibo.proxy;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class ProxyImp implements Proxy {
	Logger logger = Logger.getLogger(ProxyImpLocal.class);
	HttpClient client = new DefaultHttpClient();
	Random random = new Random(System.currentTimeMillis());
	static final String proxyUrl0 = "https://www.zxproxy.com/includes/process.php?action=update";
	

	static final String proxyUrl2 = "http://s1.8qi8.com/includes/process.php?action=update";
	static final String proxyUrl3 = "http://s3.youxiditu.com/includes/process.php?action=update";
	static final String proxyUrl4 = "https://www.zxproxy.com/includes/process.php?action=update";
	static final String proxyUrl5 = "http://s1.99daili.com/daili/process.php?action=update";
	static final String proxyUrl1 = "http://y.mdaili.com/daili/process.php?action=update";
	static final String proxyUrl6 = "http://www.nbdaili.info/includes/process.php?action=update";
	AtomicInteger l = new AtomicInteger(0);
	
	@Override
	public String request(String url) {
		String rurl = Arrays.asList(proxyUrl0, proxyUrl1, proxyUrl2, 
				proxyUrl3, proxyUrl4, proxyUrl5, proxyUrl6).get(l.getAndAdd(1) % 7);
		HttpPost post = new HttpPost(rurl);
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		//logger.info("Http get:" + url);
		qparams.add(new BasicNameValuePair("u", url));
		qparams.add(new BasicNameValuePair("allowCookies", "stripJS"));
		qparams.add(new BasicNameValuePair("stripJS", "on"));
		qparams.add(new BasicNameValuePair("stripObjects", "on"));
		
		qparams.add(new BasicNameValuePair("type", "0"));

		UrlEncodedFormEntity params = null;
		try {
			params = new UrlEncodedFormEntity(qparams, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		post.setEntity(params);

		try {
			HttpResponse response = client.execute(post);
			
			Header location = response.getFirstHeader("Location");
			//System.out.println(location.getValue());
			post.abort();
			if (location != null && location.getValue().length() > 0) {
				HttpGet get = new HttpGet(location.getValue());
				HttpResponse getResponse = client.execute(get);
				String page = EntityUtils.toString(getResponse.getEntity(),
						"UTF-8");
				get.abort();
				return page;
			}
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	
	public static void main(String[] args) {
		
		ProxyImp proxy = new ProxyImp();
//		String html = 
//			proxy.request("http://okhenha.appspot.com/rpc?param=_flJWlr5trlz8Q2OyLGGzn88O75gNVdNdN80h4ePvDFyx73yLsLpHUYkuWs__" +
//					"F63cjEI3Pby3CXbqfteaksh6u4JBUPYXNHXD5za7XRNZb4530NeyQU4RJ-5UMJ0bhxoMKtahGj7l64" +
//					"pwrEo7tIoaf3M5ryAgNKv_uFVC2cvgTPvF7cZTFV8QCUSRWUQnICiXfSsJdnD0HflIZ2DFWKhj5mq-" +
//					"JjqwCc1ganOJJ0iwl38QripuRT-346OgL3sfACHv9Lo_-fIpZ-HseCosArQdChRelB__ZeVqMKptJ-" +
//					"-I6d-okAtolfVUIBHFPAUDf-j1SQVdXkkdjH9m_hqpO4_Sw");
//		String html = proxy.request("http://okhenha.appspot.com/rpc?param=_flJWlr5trlz8Q2OyLGGzn88O75gNVdNdN80h4ePvDFyx73yLsLpHUYkuWs__F63cjEI3Pby3CXbqfteaksh6u4JBUPYX");
//		System.out.println(html);
		String html = proxy.request("http://okhenha.appspot.com/rpc?param=2RfpQCiydW4S6bSqiA_1Fg");
		System.out.println(html);
	}

}
