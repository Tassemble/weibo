package com.techq.weibo.workers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.techq.weibo.workers.meta.PPData;


public class PPCrawlerWorker extends Thread {
	static Logger LOG = Logger.getLogger(PPCrawlerWorker.class);
	PPLogin pp;

	/**
	 * weiboaccount randomnum tidnum pagenum
	 * @param user
	 * @param pwd
	 */
	int totalType = 29;
	
	//http://t.pp.cc/time/index.php?mod=library&action=index&account=2085723430&tid=8
	static String refer = "http://t.pp.cc/time/index.php?mod=library&action=index&account=weiboaccount&tid=tidnum&keyword=&page=pagenum";
	static String urls = "http://t.pp.cc/time/index.php?mod=library&action=show&account=weiboaccount&random=randomnum&tid=tidnum&page=pagenum&keyword=";
	String uid;
	HttpClient client;
	Random random = new Random(System.currentTimeMillis());
	final String contentType ;
	int pageNum = -1;
	
	/**
	 * 
	 * @param user
	 * @param pwd
	 * @param weiboUid
	 * @param type content type default is 5
	 * @throws IOException
	 */
	public PPCrawlerWorker(String user, String pwd, String weiboUid, String type) throws IOException {
		super();
		this.pp = new PPLogin(user, pwd);
		this.uid = weiboUid;
		client = login();
		this.contentType = type;
	}

	public HttpClient login() throws IOException {
		return pp.login();
	}

	public void testIT(String url) {
		pp.testAPage(url);
	}
	
	public String get(String url) throws ClientProtocolException, IOException {
		HttpGet get = new HttpGet(url);
		HttpResponse response = client.execute(get);
		return EntityUtils.toString(response.getEntity());		
	}

	public List<PPData> crawlSentences() throws ClientProtocolException, IOException {
		// http://weibo.pp.cc/time/index.php?mod=content&action=show&account=2085723430&random=994&tid=5&page=2&keyword=
	//weiboaccount randomnum tidnum pagenum
		String nextType = contentType;
		if (contentType.equals("-1")) {
			//random
			nextType = String.valueOf(random.nextInt(totalType));
			pageNum = pageNum(nextType);
		} else {
			//not random just need first crawl
			if (pageNum == -1)
				pageNum = pageNum(contentType);
		}
		String url = urls
				.replace("weiboaccount", uid)
				.replace("randomnum", String.valueOf(random.nextInt(1000)))
				.replace("tidnum", nextType)
				.replace("pagenum", String.valueOf(random.nextInt(pageNum)));
		System.out.println("http get:" + url);
		return parse(this.get(url));
	}
	
	
	public int pageNum(String type) throws ClientProtocolException, IOException {
		String url = urls
				.replace("weiboaccount", uid)
				.replace("randomnum", String.valueOf(random.nextInt(1000)))
				.replace("tidnum", type)
				.replace("pagenum", "1");
//		String refer 
		//"lastpage":779
		
		String html = this.get(url);
		
		Pattern p = Pattern.compile("\"lastpage\":([0-9]+)");
		Matcher m = p.matcher(html);
		if (m.find()) {
			return Integer.valueOf(m.group(1));
		}
		return 100;
	}

	class PPLogin {
		public PPLogin(String username, String password) {
			super();
			this.username = username;
			this.password = password;
		}

		String url = "http://t.pp.cc/member.php?mod=login&action=dologin";
		HttpClient client = null;;
		String username;
		String password;

		public HttpClient login() throws IOException {
			int error = 0;
			try {
				client = makeMutilHttpClient();
				HttpPost post = new HttpPost(url);
				makeLoginHeader(post);
				post.setEntity(makeLoginDetail());
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == 200)
					System.out.println("success");
				else {
					System.out.println("failed");
				}
				return client;
			} catch (UnsupportedEncodingException e) {
				error = 1;
			} catch (ClientProtocolException e) {
				error = 1;
			} catch (IOException e) {
				error = 1;
			}
			if (error == 1)
				throw new IOException("login error happened");
			return null;

		}

		void testAPage(String url) {
			try {
				System.out.println(EntityUtils.toString(client.execute(
						new HttpGet(url)).getEntity()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		void makeLoginHeader(HttpPost post) {
			post.setHeader("Host", "weibo.pp.cc");
			post.setHeader("Referer", "http://weibo.pp.cc/time/");
		}

		HttpEntity makeLoginDetail() throws UnsupportedEncodingException {
			// http://weibo.pp.cc/time/
			List<NameValuePair> kv = new ArrayList<NameValuePair>();
			kv.add(new BasicNameValuePair("password", password));
			kv.add(new BasicNameValuePair("username", username));
			HttpEntity entity = new UrlEncodedFormEntity(kv);
			return entity;
		}

		private HttpClient makeMutilHttpClient() {
			HttpParams params = new BasicHttpParams();
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			ClientConnectionManager cm = new ThreadSafeClientConnManager(
					params, registry);
			HttpClient client = new DefaultHttpClient(cm, params);

			return client;
		}

	}
	
	
	public static List<PPData> parse(String json){
		System.out.println(json);
//		Pattern p = Pattern.compile("\"path\":\"(content.*?)\"},\"content\":\"(.*?)\"");
		//
		Pattern p = Pattern.compile("<textarea.*?>(.*?)<\\\\/textarea>.*?http:(.*?).jpg");
		Matcher m = p.matcher(json);
		List<PPData> list = new ArrayList<PPData>();
		while (m.find()) { 
			System.out.println(m.group(1));
			LOG.info(m.group(1) + ", " + m.group(2));
			String c = null;
			try {
				c = convert(m.group(1));
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);				;
				continue;
			}
			if (c != null) {
				PPData ppdata =new PPData(c, "http:" + m.group(2).replace("\\", "") + ".jpg");
				list.add(ppdata);
				LOG.info(ppdata);
			}
		}
		return list;

	}
	
	public static String convert(String source) {
		source += " ";
		//System.out.println(source.length());
		if (null == source || " ".equals(source)) {
			return source;
		}

		StringBuffer sb = new StringBuffer();
		int i = 0;
		while (i < source.length()) {
			if (source.charAt(i) == '\\') {
				if (i + 6 >= source.length()) {
					LOG.error("unicode decode error, index is out:" + (i + 6) + ", origin:" + source);
					return null;
				}
				int j = -1;
				try {
					j = Integer.parseInt(source.substring(i + 2, i + 6), 16);
				} catch (NumberFormatException e) {
					LOG.error("convert error:" + e.getMessage(), e);
					return sb.toString();
				}
				sb.append((char) j);
				i += 6;
			} else {
				sb.append(source.charAt(i));
				i++;
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args) throws IOException {
//		String uniString1 = "\u5973\u4eba\u6700\u5927\u7684\u654c\u4eba\uff0c\u4e0d\u662f\u65f6\u95f4\uff0c\u4e0d\u662f\u6743\u5229\uff0c\u4e0d\u662f\u91d1\u94b1\uff0c\u800c\u662f\u81ea\u5df1\u7684\u504f\u6267\uff0c\u5bf9\u4e0d\u8be5\u7231\u7684\u7537\u4eba\u5374\u7231\u7684\u65e0\u6cd5\u81ea\u62d4\u3002\u9664\u4e86\u4f60\u7684\u7236\u4eb2\uff0c\u548c\u4f60\u5c06\u6765\u7684\u8001\u516c\uff0c\u8c01\u4e5f\u4e0d\u503c\u5f97\u4f60\u4ed8\u51fa\u592a\u591a\uff0c\u4e3a\u7236\u4eb2\u4ed8\u51fa\uff0c\u56e0\u4e3a\u4ed6\u7ed9\u4e86\u4f60\u4e0a\u534a\u8f88\u5b50\u7684\u7231\uff0c\u4e3a\u8001\u516c\u4ed8\u51fa\uff0c\u56e0\u4e3a\u4e0b\u534a\u8f88\u5b50\u7684\u7231\uff0c\u5c06\u4f1a\u662f\u4ed6\u6765\u7ed9\u3002";
//		String uniString = "\\\\u5973\\\\u4eba\\\\u6700\\\\u5927\\\\u7684\\\\u654c\\\\u4eba\\\\uff0c\\\\u4e0d\\\\u662f\\\\u65f6\\\\u95f4\\\\uff0c\\\\u4e0d\\\\u662f\\\\u6743\\\\u5229\\\\uff0c\\\\u4e0d\\\\u662f\\\\u91d1\\\\u94b1\\\\uff0c\\\\u800c\\\\u662f\\\\u81ea\\\\u5df1\\\\u7684\\\\u504f\\\\u6267\\\\uff0c\\\\u5bf9\\\\u4e0d\\\\u8be5\\\\u7231\\\\u7684\\\\u7537\\\\u4eba\\\\u5374\\\\u7231\\\\u7684\\\\u65e0\\\\u6cd5\\\\u81ea\\\\u62d4\\\\u3002\\\\u9664\\\\u4e86\\\\u4f60\\\\u7684\\\\u7236\\\\u4eb2\\\\uff0c\\\\u548c\\\\u4f60\\\\u5c06\\\\u6765\\\\u7684\\\\u8001\\\\u516c\\\\uff0c\\\\u8c01\\\\u4e5f\\\\u4e0d\\\\u503c\\\\u5f97\\\\u4f60\\\\u4ed8\\\\u51fa\\\\u592a\\\\u591a\\\\uff0c\\\\u4e3a\\\\u7236\\\\u4eb2\\\\u4ed8\\\\u51fa\\\\uff0c\\\\u56e0\\\\u4e3a\\\\u4ed6\\\\u7ed9\\\\u4e86\\\\u4f60\\\\u4e0a\\\\u534a\\\\u8f88\\\\u5b50\\\\u7684\\\\u7231\\\\uff0c\\\\u4e3a\\\\u8001\\\\u516c\\\\u4ed8\\\\u51fa\\\\uff0c\\\\u56e0\\\\u4e3a\\\\u4e0b\\\\u534a\\\\u8f88\\\\u5b50\\\\u7684\\\\u7231\\\\uff0c\\\\u5c06\\\\u4f1a\\\\u662f\\\\u4ed6\\\\u6765\\\\u7ed9\\\\u3002";
//		System.out.println(uniString);
//		System.out.println(uniString.replaceAll("\\\\u", "\\u"));
////		System.out.println(uniString1);
////		String re  = convert(uniString);
////		System.out.println(re);
		PPCrawlerWorker worker = new PPCrawlerWorker("luanlexi@163.com", "henjiandan","2085723430", "8");
		System.out.println(worker.crawlSentences());
//		System.out.println("\u539f\u521b\u4e8c\u7ef4\u52a8\u753b\u77ed\u7247\u3010\u671d\u671d\u5915\u5915\u3011");
//		parse("<textarea class=\\\"b_nrk_n>abc<\\/textarea>sdhfidsfjosj< http:\\/\\/conent\\/201212\\/15.jpg_djif");
	}
	
}
