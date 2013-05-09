package com.techq.weibo.share;

import com.techq.weibo.api.Utils;
import com.techq.weibo.api.imp.UtilsImp;
import com.techq.weibo.meta.User;


public class Share {
	
	public static User user ;
	
	public static Utils utils = new UtilsImp();
	
	
	public static void testStart(Utils newUtils) {
		utils = newUtils;
	}
	
	public static void testEnd() {
		utils = new UtilsImp();
	}
	
}
