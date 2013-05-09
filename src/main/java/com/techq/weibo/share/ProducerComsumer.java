package com.techq.weibo.share;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.techq.weibo.meta.Task;

public class ProducerComsumer<T extends Runnable> implements Runnable {
	Logger logger = Logger.getLogger(ProducerComsumer.class);
	
	protected BlockingQueue<T> queue;
	protected volatile boolean stop = false;
	protected boolean isStart = false;
	

	public ProducerComsumer(BlockingQueue<T> queue) {
		super();
		this.queue = queue;
	}

	public T get() {
		try {
			return queue.take();
		} catch (InterruptedException e) {

			logger.error(e);
			e.printStackTrace();
		}
		return null;
	}
	
	public void consume()
	{
		try {
			T task = queue.take();
			if (task instanceof Task) {
				Share.utils.submitTask((Task)task);
			} else {
				logger.warn("comsume is not submit to schdulePool, instead it run suddenly!");
				task.run();
			}
			
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	public void add(T food) {
		try {
			queue.put(food);
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		}
	}
	
	
	public boolean addAll(Collection<? extends T> foods) {
		return queue.addAll(foods);
	}

	@Override
	public void run() {
		while(!stop) {
			consume();
		}
	}
	
	public void start() {
		this.isStart = true;
		this.run();
	}
	
	public void stop() {
		this.stop = true;
	}
	
	public void resume() {
		this.stop = false;
	}
	
	
	public boolean isStarted() {
		return this.isStart;
	}

	
	
	
	
}
