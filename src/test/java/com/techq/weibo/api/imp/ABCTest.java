package com.techq.weibo.api.imp;

import org.apache.http.client.HttpClient;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.techq.weibo.WeiboRobot;
import com.techq.weibo.workers.DeleteFanWorker;

public class ABCTest {
	
	@Autowired
	HttpClient httpClient;
	
	@Ignore
	@Test
	public void testDelete() throws Exception {
		WeiboRobot robot = new WeiboRobot();
		
		robot.login(httpClient, "hisjhdf@12.com", "test");
		
		DeleteFanWorker d = new DeleteFanWorker(httpClient, robot.getUid(), 5);
		d.start();
		d.join();
//		d.deleteFollowing("1805464273");
		
		
	}
}
