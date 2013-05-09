package com.techq.weibo.share;

import java.util.concurrent.BlockingQueue;

import org.junit.Test;

import com.techq.weibo.meta.Task;

public class Producer extends ProducerComsumer<Task> {
	
	
	
	public Producer(BlockingQueue<Task> queue) {
		super(queue);
	}

	@Test
	public void run() {
		
	}
}
