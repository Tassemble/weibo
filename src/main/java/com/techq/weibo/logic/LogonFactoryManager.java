package com.techq.weibo.logic;

import com.techq.weibo.api.LogonHandler;
import com.techq.weibo.api.imp.Ssologin142Handler;

/**
 * 
 * @author CHQ
 * @since  2013-2-12
 */
public class LogonFactoryManager {
	
	public static LogonHandler getLogonHandler() {
		return new Ssologin142Handler();
	}

}
