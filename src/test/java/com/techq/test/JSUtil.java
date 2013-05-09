package com.techq.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class JSUtil {

	public static void test() {
		ScriptEngineManager mgr = new ScriptEngineManager(); 
		ScriptEngine engine = mgr.getEngineByName("JavaScript");
		Reader reader = null;
		try {
			reader = new BufferedReader(new FileReader("/home/chq/workspace/weibo-demo/src/com/techq/test/test.js"));
			engine.eval(reader);

			Invocable inv = (Invocable) engine;     
			
			String value = String.valueOf(inv.invokeFunction("parseURL", "http/okhenha/appspot/com/"));  
			System.out.println(value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void encode() {
		String a = "http://okhenha.appspot.com/";
//		a.replace('/./', '/')
		
	}
	public static void main(String[] args) {
		JSUtil.test();
	}
	
}
