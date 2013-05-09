/**
 * 
 */
package com.techq.weibo.bean;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.techq.weibo.WeiboRobot;

/**
 * @author CHQ
 * @since  2013-2-13
 */
public class WeiboBeanFactory {
	
	
	static ClassPathXmlApplicationContext context ;
	
	
	static {
		context = new ClassPathXmlApplicationContext(
				"classpath:/applicationContext-aop-base.xml", 
				"classpath:/applicationContext-bo.xml");
	}
	
	
	public static void main(String[] args) {
		
	}
	
	public static WeiboRobot getWeiboRobot() {
		return context.getBean(WeiboRobot.class);
	}
	
	public static ClassPathXmlApplicationContext getContext() {
		return context;
	}
	
}
