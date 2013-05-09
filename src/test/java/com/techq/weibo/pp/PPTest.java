package com.techq.weibo.pp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;


import com.techq.weibo.workers.PPCrawlerWorker;
import com.techq.weibo.workers.meta.PPData;

public class PPTest {

	@Ignore
	@Test
	public void test() throws IOException {
		PPCrawlerWorker crawler = new PPCrawlerWorker("luanlexi@163.com", "henjiandan", "2085723430", "-1");
		//crawler.login();
		// crawler.testIT("http://weibo.pp.cc/time/");
		//crawler.testIT("http://weibo.pp.cc/time/index.php?mod=content&action=show&account=2085723430&random=994&tid=5&page=2&keyword=");
		//
		for(PPData p: crawler.crawlSentences()) {
			System.out.println(p);
		}
	}
	
	@Ignore
	@Test
	public void testTime() {
		// TODO Auto-generated method stub
		System.out.println(System.currentTimeMillis());
	}

	@Ignore
	@Test
	public void testWord() {
		System.out
				.println("\u7cfb\u7edf\u7e41\u5fd9\uff0c\u8bf7\u7a0d\u5019\u518d\u8bd5\u5427\u3002");
	}

	// @Ignore
	@Test
	public void extractJson() throws IOException {
		String html = readFile("/home/chq/work/git/works/share-projects/weibo-client/relate/pp");
		Pattern p = Pattern.compile("\"path\":\"(content.*?)\"},\"content\":\"(.*?)\"");
		Matcher m = p.matcher(html);
		while (m.find()) {

			// System.out.println(m.group(1).replace("\\u", ""));
			System.out.println(m.group(1).replace("\\", "") + " " + convert(m.group(2)));
			// for(char c : m.group(1).toCharArray())
			// System.out.print(c);
			// System.out.println();
			// System.out.println(m.group(1).replace("\\u", "\\\\u"));
		}

	}

	public static String convert(String source) {
		if (null == source || " ".equals(source)) {
			return source;
		}

		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < source.length()) {
			if (source.charAt(i) == '\\') {
				int j = Integer.parseInt(source.substring(i + 2, i + 6), 16);
				sb.append((char) j);
				i += 6;
			} else {
				sb.append(source.charAt(i));
				i++;
			}
		}
		return sb.toString();
	}

	private String readFile(String path) throws IOException {
		File f = new File(path);
		BufferedReader is = new BufferedReader(new FileReader(f));
		String line = is.readLine();
		StringBuffer buffer = new StringBuffer(1000);
		do {
			if (line != null)
				buffer.append(line);
			else {
				break;
			}
			line = is.readLine();
		} while (true);
		return buffer.toString();
	}
}
