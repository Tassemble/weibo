package com.techq.weibo.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonUtils {

	static Gson gson = new GsonBuilder().serializeNulls().create();
	
	public static void printJson(Object followersFromGroup) {
		// TODO Auto-generated method stub
		System.out.println(gson.toJson(followersFromGroup));
	}

}
