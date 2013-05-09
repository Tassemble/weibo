package com.techq.weibo.workers.meta;

public class WeiboPubInfo {

	
	public boolean pubWeibo = true;
	//20 min
	public int time = 20;
	
	public String device = "ipad";
	
	public String contentType = "5";

	@Override
	public String toString() {
		return "WeiboPubInfo [pubWeibo=" + pubWeibo + ", time=" + time
				+ ", device=" + device + ", contentType=" + contentType + "]";
	}
	
	

	
	
}
