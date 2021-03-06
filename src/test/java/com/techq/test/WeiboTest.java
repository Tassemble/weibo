package com.techq.test;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.techq.weibo.WeiboClient;
import com.techq.weibo.api.Utils;
import com.techq.weibo.api.imp.UtilsImp;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.proxy.ProxyImpLocal;
import com.techq.weibo.share.Share;

public class WeiboTest {
	Logger logger = Logger.getLogger(WeiboTest.class);

	private final Utils utils = new UtilsImp() {
		@Override
		public String httpGet(HttpClient client, String url)
				throws WeiboException {
			return new ProxyImpLocal().request(url);
		}
	};

	// @Test
	// public void start() {
	// Share.testStart(utils);
	// // if user already stored then read info from server
	// // else store user info to server
	// WeiboClient client = new WeiboClient();
	// String email = "hisjhdf@12.com";
	// String password = "123456";
	// client.start(email, password);
	// }

	@Test
	public void test() {
		Share.testStart(utils);
		// if user already stored then read info from server
		// else store user info to server
		final WeiboClient client = new WeiboClient();
		// for (int i = 1; i < 2; i++) {
		// final int t = 1772403527;
		// new Thread(){
		// @Override
		// public void run() {
		// client.start("hisjhdf@12.com", "test");
		// }
		// }.start();
		// }
//		new Thread() {
//			@Override
//			public void run() {
//				client.start("hisjhdf@12.com", "test");
//			}
//		}.start();
		client.start("hisjhdf@12.com", "test");
//	client.start("hisjhdf@12.com", "123456");
	}

}
