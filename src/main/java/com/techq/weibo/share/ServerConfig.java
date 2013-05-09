package com.techq.weibo.share;

public class ServerConfig {
	/**
	 * 心跳时间 单位是秒，表示一次心跳的间隔
	 */
	public final static int HEART_BEAT_TIME = 60;
	
	/**
	 * 设置客户端活跃的时间，如果在规定秒内没有反映，则表示下线
	 * 每次心跳则会更新为最新，即存活时间
	 */
	public final static int CLIENT_ALIVE_TIME = 100;


	
}
