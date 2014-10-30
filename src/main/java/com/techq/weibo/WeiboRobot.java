package com.techq.weibo;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
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
import org.springframework.beans.factory.annotation.Autowired;

import com.techq.weibo.api.ClientRobot;
import com.techq.weibo.api.UserInfoAPI;
import com.techq.weibo.api.WeiboAPI;
import com.techq.weibo.api.imp.UserInfoAPIImp;
import com.techq.weibo.bean.WeiboBeanFactory;
import com.techq.weibo.crypto.Crypto;
import com.techq.weibo.domain.WeiboConfig;
import com.techq.weibo.exception.WeiboException;
import com.techq.weibo.meta.Task;
import com.techq.weibo.meta.User;
import com.techq.weibo.workers.DeleteFanWorker;
import com.techq.weibo.workers.PPCrawlerWorker;
import com.techq.weibo.workers.ShareWorker;
import com.techq.weibo.workers.meta.WeiboPubInfo;

public class WeiboRobot implements ClientRobot {
	private final static Logger LOG = Logger.getLogger(WeiboRobot.class);
	@Autowired
	WeiboConfig weiboConfig;

	@Autowired
	WeiboAPI weiboAPI;

	public final static String WEIBO_HOST = "http://weibo.com/";

	@Autowired
	HttpClient httpClient;

	public boolean login(HttpClient client, String email, String pwd) throws Exception {
		if (weiboConfig.getCheckCodeSwitchOpen()) {
			String code = readCheckerCode(client);
			return weiboAPI.logon(client, email, pwd, code);
		}

		return weiboAPI.logon(client, email, pwd);
	}

	// 714603
	public boolean sayInGroup(long group, String word) throws ClientProtocolException, IOException {

		// http://q.weibo.com/ajax/mblog/add
		HttpPost post = new HttpPost("http://q.weibo.com/ajax/mblog/add");
		// text=%E4%BA%92%E7%B2%89%EF%BC%8C1%E7%A7%92%E5%9B%9E%EF%BC%81&pic_id=&gid=714603&forward=0&_t=0
		post.setHeader("Host", "q.weibo.com");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0");
		post.setHeader("Referer", "http://q.weibo.com/" + group + "?topnav=1");
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> qparams = new ArrayList<NameValuePair>();
		qparams.add(new BasicNameValuePair("text", word));
		qparams.add(new BasicNameValuePair("gid", String.valueOf(group)));
		// forward
		qparams.add(new BasicNameValuePair("forward", "0"));
		UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
		post.setEntity(params);

		httpClient.getConnectionManager().closeExpiredConnections();
		HttpResponse response = httpClient.execute(post);

		HttpEntity entity = response.getEntity();
		String page = EntityUtils.toString(entity, "UTF-8");

		post.abort();
		String result = isSuccessful(page);
		if (result.equals(SUCCESS)) {
			LOG.info("saying in group is success, reponse code:" + SUCCESS);
			return true;
		}
		LOG.warn("saying in group failed, reponse code:" + result);
		return false;

		// text=%E4%BA%92%E7%B2%89%EF%BC%8C1%E7%A7%92%E5%9B%9E%EF%BC%81&pic_id=&gid=714603&forward=0&_t=0
	}

	public String get(HttpClient client, String url) throws IOException {
		LOG.info("http get:" + url);
		HttpGet get = new HttpGet(url);
		client.getConnectionManager().closeExpiredConnections();

		HttpResponse response = client.execute(get);

		// System.out.println(response.getStatusLine());
		HttpEntity entity = response.getEntity();

		String result = EntityUtils.toString(entity, "UTF-8");
		get.abort();

		return result;
	}

	// 2120715483

	// change some time later
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.techq.weibo.Robot#follow(java.lang.String, java.lang.String)
	 */
	public boolean follow(HttpClient client, long currentUserId, long followId) {
		try {
			HttpPost post;
			post = new HttpPost("http://weibo.com/aj/f/followed?__rnd=1328973883555");
			List<NameValuePair> qparams = new ArrayList<NameValuePair>();
			qparams.add(new BasicNameValuePair("uid", String.valueOf(followId)));
			qparams.add(new BasicNameValuePair("wforce", "l"));
			qparams.add(new BasicNameValuePair("f", "l"));
			qparams.add(new BasicNameValuePair("refer_sort", "profile"));
			qparams.add(new BasicNameValuePair("location", "profile"));
			UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
			post.setEntity(params);
			post.setHeader("Referer", "http://weibo.com/" + String.valueOf(followId));
			HttpResponse response = client.execute(post);

			HttpEntity entity = response.getEntity();
			String page = EntityUtils.toString(entity, "UTF-8");

			String result = isSuccessful(page);
			if (result.equals(SUCCESS)) {
				LOG.info("follow is success, reponse code:" + SUCCESS);
				return true;
			}
			LOG.warn("follow is fail, reponse code:" + result);
			post.abort();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}
	
	
	

	public String isSuccessful(String page) {
		LOG.debug(page);
		int start = page.indexOf("{\"code\":\"");
		if (start != -1) {
			start += "{\"code\":\"".length();
			int end = page.indexOf("\"", start);
			// System.out.println(page);
			String statusCode = page.substring(start, end);
			if (statusCode.equalsIgnoreCase("100000"))
				return SUCCESS;
			return statusCode;
		}
		return UNKOWN_CODE;
	}

	private static String readCheckerCode(HttpClient client) throws IOException, ClientProtocolException {
		HttpClientParams.setCookiePolicy(client.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);

		HttpResponse response = client.execute(new HttpGet(
				"http://login.sina.com.cn/cgi/pin.php?r=64755100&s=0&p=472f6e920f0b4d2529a8a318163da4fd4952"));
		InputStream is = response.getEntity().getContent();
		// Read from an input stream
		// Image image = outputScreen(is);

		RenderedImage img = ImageIO.read(is);
		ImageIO.write(img, "png", new File("CheckCode.png"));
		LOG.info("please enter check code:");
		Scanner scanner = new Scanner(System.in);
		String code = scanner.nextLine();

		return code;
	}

	public static String outputChcekCodeScreen(InputStream is) throws IOException {
		Image image = ImageIO.read(is);

		// Use a label to display the image
		JFrame frame;
		try {
			frame = new JFrame();
			JLabel label = new JLabel(new ImageIcon(image));
			frame.getContentPane().add(label, BorderLayout.CENTER);
			frame.pack();
			frame.setLocation(400, 400);
			frame.setVisible(true);

			Scanner scanner = new Scanner(System.in);
			String code = scanner.nextLine();
			frame.dispose();
			return code;
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage(), e);

			return null;
		} finally {

		}
	}

	public static final String SUCCESS = "success";
	public static final String UNKOWN_CODE = "unkonw code";
	public static final String HOST = "http://okhenha.appspot.com/";

	public void testLog4j() {
		LOG.info("ok now");
	}

	// public void

	public boolean checkFollowFromWeibo(String html) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("can use this function");
	}

	public boolean checkIfFollowed(String userId) throws WeiboException {
		String content;
		try {
			content = this.get(WEIBO_HOST + userId);
		} catch (IOException e) {
			LOG.error("checkIfFollowed", e);
			throw new WeiboException(WeiboException.INTERNAL_ERROR, "checkIfFollowed", e);
		}

		String regex = "id=\"atnRelation\">([\u4e00-\u9fa5]*)<span class";
		List<String> list = UserInfoAPIImp.getResult(content, regex);

		if (list.size() > 0 && list.get(0).contains("关注"))
			return true;
		return false;
	}

	public User getUserInfoFromWeibo(String html) {

		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("can use this function");
	}

	List<Long> findFollowers(String html) throws UnexpectedException {
		int start = 0;
		List<Long> list = new ArrayList<Long>();
		long totalLen = html.length();
		for (; (start = html.indexOf("addFollow", start)) > 0;) {
			int idStart = html.indexOf("reportspam?rid=", start) + "reportspam?rid=".length();
			int idEnd = html.indexOf("&from=", idStart);
			if (idEnd < 0 || idStart < 0 || idStart > totalLen || idEnd > totalLen)
				throw new UnexpectedException("idStart:" + idStart + ", idEnd:" + idEnd);
			list.add(Long.valueOf(html.substring(idStart, idEnd)));
			start = idEnd;
		}
		return list;
	}

	public String getFollowersPage(HttpClient client, long uid, long pageId) throws ParseException, IOException {
		HttpGet get = new HttpGet("http://weibo.com/" + uid + "/myfans?t=1&f=1&page=" + pageId);
		HttpResponse response = client.execute(get);

		HttpEntity entity = response.getEntity();
		String page = EntityUtils.toString(entity, "UTF-8");
		get.abort();
		return page;
	}

	List<String> words = null;
	Set<String> groups = null;
	public String email = null;
	public String password = null;
	List<String> expression = null;
	String proxyfollow = null;
	String proxyemail = null;
	String proxypass = null;
	Map<String, Integer> groupsFailedTime = null;
	int pauseTime = 0;
	int logout = 0;
	int pauseInterval = 0;
	/**
	 * dafault time to send msg to group
	 */
	int sendTime = 60;

	/**
	 * delele followers
	 * 
	 * @throws IOException
	 */
	boolean deleteFollowers = false;
	int delelteFollowersInterval = 10;

	/**
	 * check code
	 * 
	 * @throws IOException
	 */
	boolean checkCode = false;
	/**
	 * first time
	 * 
	 * @throws IOException
	 */
	static int firstTime = 1;

	/**
	 * 
	 * @throws IOException
	 */
	WeiboPubInfo weiboPubInfo = new WeiboPubInfo();

	public void readFromProperty() throws IOException {
		words = new ArrayList<String>();
		groups = new HashSet<String>();
		expression = new ArrayList<String>();
		groupsFailedTime = new HashMap<String, Integer>();
		Properties properties = new Properties();
		try {
			properties.load(new InputStreamReader(new FileInputStream("conf/config.property"), "UTF-8"));
			String values = (String) properties.get("words");
			for (String value : values.split(";")) {
				LOG.info("add value:" + value);
				words.add(value);
			}

			values = (String) properties.get("groups");
			for (String value : values.split(";")) {
				LOG.info("add group:" + value);
				groups.add(value);
				// failed time 5 try
				groupsFailedTime.put(value, 5);
			}
			values = (String) properties.get("expression");
			for (String value : values.split(";")) {
				LOG.info("add expression:" + value);
				expression.add(value);
			}

			email = (String) properties.get("email");
			password = crypto.decrypt((String) properties.get("password"));

			proxyfollow = (String) properties.getProperty("proxyfollow");

			if (proxyfollow.equalsIgnoreCase("true")) {
				proxyemail = (String) properties.getProperty("proxyemail");
				proxypass = crypto.decrypt((String) properties.getProperty("proxypass"));
			}
			pauseTime = Integer.valueOf((String) properties.getProperty("pause"));
			LOG.info("pause time : " + pauseTime);
			logout = Integer.valueOf((String) properties.getProperty("logout"));
			LOG.info("logout time:" + logout);
			pauseInterval = Integer.valueOf((String) properties.getProperty("PauseInterval"));
			LOG.info("PauseInterval time:" + pauseInterval);
			sendTime = Integer.valueOf((String) properties.getProperty("SendTime"));
			LOG.info("Send time:" + sendTime);

			firstTime = Integer.valueOf((String) properties.getProperty("FirstTime"));

			String check = (String) properties.get("CheckCode");
			if (check != null && check.equalsIgnoreCase("true")) {
				checkCode = true;
			}
			String delF[] = ((String) properties.getProperty("DeleteFollowers")).split(":");
			assert (delF.length == 2);
			if (delF[0] != null && delF[0].equalsIgnoreCase("true")) {
				delelteFollowersInterval = Integer.valueOf(delF[1]);
				deleteFollowers = true;
			}

			String followFans = properties.getProperty("FollowFans");

			LOG.info("Send time:" + sendTime);

			String weiboPubs[] = ((String) properties.getProperty("WeiboPub")).split(":");
			assert (weiboPubs != null && weiboPubs.length == 4);
			if (weiboPubs[0].equalsIgnoreCase("true")) {
				weiboPubInfo.pubWeibo = true;
				weiboPubInfo.time = Integer.valueOf(weiboPubs[1]) < 10 ? 10 : Integer.valueOf(weiboPubs[1]);
				weiboPubInfo.device = weiboPubs[2];
				weiboPubInfo.contentType = weiboPubs[3];
				LOG.info(weiboPubInfo);
			} else {
				weiboPubInfo.pubWeibo = false;
			}
		} catch (IOException e) {
			throw new IOException("read propertity exception", e);
		}

	}

	public void shutdown() {
		sayingWorker.halt();
		followingWorker.halt();
		timeWorker.halt();
		deleteFanWorker.shutdown();
		shareWorker.shutdown();
	}

	Set<Long> failedGroup = new HashSet<Long>();

	void removeGroup(Set<String> groups, String group) {
		synchronized (groups) {
			groups.remove(group);
		}
	}

	void add2Group(Set<String> groups, String group) {
		synchronized (this) {
			groups.add(group);
		}
	}

	public class GroupSayingWorker extends Thread {

		boolean isRunning = true;

		String email;
		String pwd;
		int time = 0;
		WeiboRobot robot;
		volatile boolean pauseNow = false;

		public GroupSayingWorker(WeiboRobot robot) {
			super("FollowerWorker");
			isRunning = true;
			this.robot = robot;
		}

		public void run() {

			while (isRunning) {
				try {
					if (pauseNow) {
						pause();
						pauseNow = false;
					}

					Random random = new Random(System.currentTimeMillis());

					CopyOnWriteArraySet<String> groupsCopy = new CopyOnWriteArraySet<String>(groups);
					for (String group : groupsCopy) {
						long g = Long.valueOf(group);
						String saying = words.get(random.nextInt(20) % words.size());

						for (int i = 0; i < random.nextInt(5); i++) {
							saying += expression.get(random.nextInt(expression.size()));
						}
						TimeUnit.SECONDS.sleep(2);
						if (hasPostInFirstGroupFeed(g, uid)) {
							LOG.info("skip this group:" + g);
							continue;
						}
						TimeUnit.SECONDS.sleep(2);
						LOG.info("say to group:" + g + ", " + saying);
						boolean isSuccessful = robot.sayInGroup(g, saying);
						if (!isSuccessful) {
							// logger.warn("failed group:" + g);
							int leftTime = groupsFailedTime.get(group);
							leftTime--;
							if (leftTime <= 0) {
								groupsFailedTime.remove(group);
								removeGroup(groups, group);
							} else {
								groupsFailedTime.put(group, leftTime);
							}
						}
						int sleepTime = sendTime + random.nextInt(20);
						while (sleepTime-- > 0) {
							if (pauseNow) {
								pause();
								pauseNow = false;
							}
							TimeUnit.SECONDS.sleep(1);
						}
					}
					time++;
					// if (time % 100 == 0) {
					// TimeUnit.MINUTES.sleep(10);
					// restart();
					// } else {
					TimeUnit.SECONDS.sleep(1);
					// }
					LOG.info("cnt:" + time);
				}  catch (Exception e) {
					LOG.error(e.getMessage(), e);
					//continue..
				}
			}
		}

		public void halt() {
			isRunning = false;
			if (this.isAlive())
				this.interrupt();
		}

		public void pause() throws InterruptedException {
			LOG.info("saying pause...");
			synchronized (this) {
				int condition = 0;
				while (condition++ < pauseTime) {
					wait(1000);
				}
			}
			LOG.info("i am awake, haha...");
		}

	}

	public void pause() {
		try {
			TimeUnit.SECONDS.sleep(pauseTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.error(e.getMessage(), e);
		}
	}

	public class TimeWorker extends Thread {

		int timeout = 0;
		volatile int tick = 0;
		boolean isRunning = true;

		public TimeWorker() {
			super("TimeWorker");
			timeout = logout;
		}

		public void run() {
			while ((timeout == 0 || timeout > tick) && isRunning) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					LOG.error(e.getMessage(), e);
					shutdown();
				}
				LOG.info("tick:" + tick + " seconds");
				tick++;
				if (pauseInterval != 0 && tick % pauseInterval == 0) {
					followingWorker.pauseNow = true;
					sayingWorker.pauseNow = true;
				}
			}

			LOG.warn("time out after " + timeout + " seconds");
			// after 30min, sleep then
			shutdown();
		}

		public void halt() {
			// timeout
			isRunning = false;
			if (this.isAlive())
				this.interrupt();
		}
	}

	public class FollowingWorker extends Thread {
		boolean isRunning = true;

		long myId;
		WeiboRobot self;
		int failedFollow = 5;
		volatile boolean pauseNow = false;

		FollowingWorker(WeiboRobot robot, long userId) {
			super("CheckerWorker");
			isRunning = true;
			myId = userId;
			self = robot;
		}

		public void run() {
			while (isRunning) {
				try {

					List<Long> uids = new ArrayList<Long>();
					String page = self.getFollowersPage(self.httpClient, uid, 1);
					uids.addAll(self.findFollowers(page));
					LOG.info("find fans number:" + uids.size());
					for (Long fid : uids) {
						boolean isOK = self.follow(self.httpClient, myId, fid);
						failedFollow = isOK ? failedFollow : (failedFollow - 1);
						LOG.info("follow uid:" + fid);
						if (failedFollow == 0) {
							LOG.warn("exit following worker!!! since there is 5 times failed");
							return;
						}
						int sleepTime = 3;
						while (sleepTime-- > 0) {
							if (pauseNow) {
								pause();
								pauseNow = false;
							}
							TimeUnit.SECONDS.sleep(1);
						}
					}
					int sleepTime = 10;
					while (sleepTime-- > 0) {
						if (pauseNow) {
							pause();
							pauseNow = false;
						}
						TimeUnit.SECONDS.sleep(1);
					}
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					//continue running 
				}

			}

		}

		public void halt() {
			isRunning = false;
			if (this.isAlive()) {
				this.interrupt();
			}
		}

		public void pause() throws InterruptedException {
			LOG.info("checking following pause...");
			synchronized (this) {
				int condition = 0;
				while (condition++ < pauseTime) {
					wait(1000);
				}
			}
			LOG.info("i start checking, haha...");
		}
	}

	volatile long uid = -1;

	GroupSayingWorker sayingWorker;

	FollowingWorker followingWorker;
	DeleteFanWorker deleteFanWorker;

	TimeWorker timeWorker;
	ShareWorker shareWorker;

	public void start(String email, String pwd) throws Exception {

		LOG.info("start...");

		validate(email, pwd);
		this.login(httpClient, email, pwd);
		String id = getUid();

		if (id == null || id != null && id.length() <= 0) {
			throw new IOException("登陆失败，获取id lenght() is smaller than 0");
		}
		uid = Long.valueOf(id);
		if (groups.size() == 0)
			LOG.warn("groups is zero, you must add some group");

		sayingWorker = new GroupSayingWorker(this);

		if (proxyfollow.equalsIgnoreCase("true")) {
			followingWorker = startProxy(this);
		} else {
			followingWorker = new FollowingWorker(this, uid);
		}

		if (deleteFollowers == true) {
			deleteFanWorker = new DeleteFanWorker(httpClient, String.valueOf(uid), delelteFollowersInterval);
			deleteFanWorker.start();
			LOG.info("start delete worker..., delete a follower each " + delelteFollowersInterval + "s");
		}

		if (weiboPubInfo.pubWeibo) {
			shareWorker = new ShareWorker(weiboPubInfo.time, new PPCrawlerWorker("hisjhdf@12.com", "henjiandan", "2085723430",
					weiboPubInfo.contentType), httpClient, weiboPubInfo.device);
			shareWorker.start();
		}

		// shutdown
		timeWorker = new TimeWorker();
		timeWorker.start();

		if (weiboConfig.isGroupSentSwitchOpen()) {
			LOG.info("start saying to group...");
			sayingWorker.start();
		}

		if (weiboConfig.isFollowFanSwitchOpen()) {
			LOG.info("start following fans...");
			followingWorker.start();
		}

		timeWorker.join();
	}

	public String getUid() throws IOException {
		String html = this.get(httpClient, "http://weibo.com/");

		UserInfoAPI api = new UserInfoAPIImp();
		String id = api.getUserId(html);
		return id;
	}

	private void validate(String email, String pwd) {
		if (email == null || pwd == null || email.length() <= 0 || pwd.length() <= 0)
			throw new IllegalArgumentException("emaiL:" + email + ", pwd:" + pwd);
		// logger.info("user:" + email + ", pwd:" + pwd);
	}

	@SuppressWarnings("deprecation")
	private FollowingWorker startProxy(WeiboRobot weiboRobot) throws Exception {
		validate(proxyemail, proxypass);
		this.login(weiboRobot.httpClient, proxyemail, proxypass);
		String html = this.get(weiboRobot.httpClient, "http://weibo.com/");
		UserInfoAPI api = new UserInfoAPIImp();
		String id = api.getUserId(html);

		if (id == null || id != null && id.length() <= 0) {
			throw new IOException("代理登陆失败，获取id lenght() is smaller than 0");
		}

		return new FollowingWorker(weiboRobot, Long.valueOf(id));
	}

	@Deprecated
	public void restart() throws Exception {
		LOG.info("restart...xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
		HttpParams params = new BasicHttpParams();
		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		ClientConnectionManager cm = new ThreadSafeClientConnManager(params, registry);
		httpClient = new DefaultHttpClient(cm, params);
		this.login(httpClient, this.email, this.password);
	}

	public static void main(String[] args) throws Exception {
		int tryTime = 0;
		while (tryTime < 3) {
			tryTime++;
			try {

				WeiboRobot robot = WeiboBeanFactory.getWeiboRobot();
				robot.readFromProperty();
				encrpy();
				robot.start(robot.email, robot.password);

				break;
				// TimeUnit.MINUTES.sleep(30 + new Random().nextInt(20));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace(System.out);
			}
		}
	}

	static Crypto crypto = new Crypto();

	private static void encrpy() throws UnsupportedEncodingException {
		if (firstTime == 1) {
			Scanner scanner = new Scanner(System.in);
			String line;
			LOG.info("请输入您的密码，同时，把加密后的密码写入到配置文件(config.property中password，并将FirstTime改为0)中，停止输入则输入end！");
			while (!(line = scanner.nextLine()).equalsIgnoreCase("end")) {
				System.out.println(crypto.encrypt(line));
			}
			System.out.println("如果已经完成配置，则按任意键");
			scanner.nextLine();
		}
	}

	@Override
	public boolean login(String user, String pwd) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("this is not supported");
	}

	@Override
	public boolean follow(long currentUserId, long followId) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("this is not supported");
	}

	@Override
	public void add2Queue(Task ask) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("this is not supported");
	}

	@Override
	public String get(String url) throws IOException {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("this is not supported");
	}

	public boolean hasSaidSomething(String html, long uid) {
		// logger.info("check contains keys: uid=\\\"" + uid + "\\\" nick=");
		LOG.info("check contains saying...");
		if (html.contains("uid=\\\"" + uid + "\\\" nick=")) {
			LOG.info("yes, you have saying just now, so don't so frequently!");
			return true;
		}
		LOG.info("there is no saying, so say loudly in this group!");
		return false;
	}

	public String postGroupFeed(long group) throws ClientProtocolException, IOException {
		HttpPost post = new HttpPost("http://q.weibo.com/ajax/mblog/groupfeed");
		post.setHeader("Host", "q.weibo.com");
		post.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:5.0) Gecko/20100101 Firefox/5.0");
		post.setHeader("Referer", "http://q.weibo.com/" + group);
		post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		List<NameValuePair> qparams = new ArrayList<NameValuePair>();

		/**
		 * mids=&new=0&gid=164491&page=1&count=10&pre_page=2&pagebar=0&_k=
		 * 132930776446561&_t=0&is_search=&uid=0&since_id=0
		 */
		qparams.add(new BasicNameValuePair("mids", ""));
		qparams.add(new BasicNameValuePair("new", "0"));
		qparams.add(new BasicNameValuePair("gid", String.valueOf(group)));
		// forward
		qparams.add(new BasicNameValuePair("page", "1"));
		qparams.add(new BasicNameValuePair("count", "10"));
		qparams.add(new BasicNameValuePair("pre_page", "2"));
		qparams.add(new BasicNameValuePair("pagebar", "0"));
		qparams.add(new BasicNameValuePair("_k", Long.toString(System.currentTimeMillis()) + ""
				+ Integer.toString((new Random().nextInt(90) + 10))));
		qparams.add(new BasicNameValuePair("_t", "0"));
		qparams.add(new BasicNameValuePair("is_search", ""));
		qparams.add(new BasicNameValuePair("uid", "0"));
		qparams.add(new BasicNameValuePair("since_id", "0"));
		UrlEncodedFormEntity params = new UrlEncodedFormEntity(qparams, "UTF-8");
		post.setEntity(params);

		HttpResponse response = httpClient.execute(post);

		HttpEntity entity = response.getEntity();
		String page = EntityUtils.toString(entity, "UTF-8");
		post.abort();
		return page;
	}

	public boolean hasPostInFirstGroupFeed(long group, long uid) throws ClientProtocolException, IOException {
		String page = postGroupFeed(group);
		return hasSaidSomething(page, uid);
	}

}
