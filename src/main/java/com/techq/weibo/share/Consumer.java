package com.techq.weibo.share;

import java.util.concurrent.BlockingQueue;

import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;

public class Consumer extends ProducerComsumer<Task> {

	public Consumer(BlockingQueue<Task> queue) {
		super(queue);
	}
	

}
