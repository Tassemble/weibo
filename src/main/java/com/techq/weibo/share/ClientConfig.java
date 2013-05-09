package com.techq.weibo.share;

public class ClientConfig {
	/**
	 * 客户端的心跳频率 单位是秒，表示间隔多长通知一下服务端
	 */
	public final static int HEART_BEAT_TIME = 60;
	
	
	/**
	 * 任务的跟踪时间，即提交任务后经过多长访问一下服务器，来检查任务是否完成
	 */
	public final static int TASK_TRACK_PERIOD = 35;
	
	/**
	 * 经过多长时间取消跟踪该任务，即便是没有完成
	 */
	public final static int CANCLE_TRACK_TIME = 60;
	
	/**
	 *   任务个数限制
	 */
	public final static int TASKS_LIMIE = 10;
	
	
	/**
	 * 经历多长时间提交任务
	 */
	
	public final static int TIME_2_SUBMIT = 20;

	/**
	 * 多长时间ask(请求)任务
	 * 
	 */
	public final static int TIME_2_ASK_TASK = 15;
	
	
	/**
	 * 多长时间去获取id(即将要来关注我的粉丝id)来关注我
	 */
	public final static int TIME_2_GET_IDS_FOLLOW_ME = 35;
	
	
	/**
	 * 多长时间去follow a -> b
	 */
	public final static int TIME_2_FOLLOW = 24;
	
	
	/**
	 * time to checkIfFollowed
	 */
	public final static int TIME_2_CHECK_IF_FOLLOWED = 29;
}
