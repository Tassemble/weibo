package com.techq.test;

import java.io.IOException;

import org.junit.Test;

import com.techq.weibo.api.WeiboInnerAPI;
import com.techq.weibo.api.imp.WeiboInnerAPIImp;

public class WeiboAPITest {

	
	@Test
	public void testHasSuchFan() throws IOException {
		WeiboInnerAPI api = new WeiboInnerAPIImp();
		api.login("hisjhdf@12.com", "123456");
		
		System.out.println(api.hasSuchFan(1772403527, 2085723430));
	}
}
