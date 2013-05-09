package com.techq.weibo.workers.meta;

public class PPData {
	public String content;
	public String url;
	public PPData(String content, String url) {
		super();
		this.content = content;
		this.url = url;
	}
	@Override
	public String toString() {
		return "PPData [content=" + content + ", url=" + url + "]";
	}
	
	
	
	
}
