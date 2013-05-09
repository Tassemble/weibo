package com.techq.weibo.proxy;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.techq.weibo.exception.WeiboException;

public class ProxyImpLocal implements Proxy {

	Logger logger = Logger.getLogger(ProxyImpLocal.class);
	HttpClient client = new DefaultHttpClient();
	
	@Override
	public String request(String url) throws WeiboException {
		String page = null;
		try {
			if (url == null)
				throw new WeiboException(WeiboException.URL_NULL_ERROR, "Get url is null", null);
			url = url.replace("http://okhenha.appspot.com/", "http://127.0.0.1:8080/");
			logger.info("Http get:" + url);
			HttpGet get = new HttpGet(url);
			HttpResponse getResponse = client.execute(get);
			page = EntityUtils.toString(getResponse.getEntity(),
					"UTF-8");
			get.abort();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return page;
	}

}
