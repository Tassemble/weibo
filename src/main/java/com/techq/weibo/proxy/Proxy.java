package com.techq.weibo.proxy;

import com.techq.weibo.exception.WeiboException;

public interface Proxy {
	public String request(String url) throws WeiboException;
}
