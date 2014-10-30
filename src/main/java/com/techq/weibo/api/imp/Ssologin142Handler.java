package com.techq.weibo.api.imp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.chainsaw.Main;

import com.techq.weibo.WeiboRobot;
import com.techq.weibo.api.LogonHandler;
import com.techq.weibo.bean.WeiboBeanFactory;

/**
 * @author CHQ
 * @since 2013-2-12
 */
public class Ssologin142Handler implements LogonHandler {

	@Override
	public Object login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static void main(String[] args) throws Exception {
		HttpClient client = new DefaultHttpClient();
		Ssologin142Handler handler = new Ssologin142Handler();
		handler.login(client, "hisjhdf@12.com", "test");
		String html = handler.get(client, "http://weibo.com/");
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

	static String SINA_PK;

	private static final Log LOG = LogFactory.getLog(Main.class);
	//door=EYG4X

	
	public boolean login(HttpClient client, String username, String password) throws Exception{
		return login(client, username, password, null);
		
	}
	
	@Override
	public boolean login(HttpClient client, String username, String password, String code) throws Exception{
		String entity;
		try {
			client.getParams().setParameter("http.protocol.cookie-policy",
					CookiePolicy.BROWSER_COMPATIBILITY);
			client.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 5000);

			HttpPost post = new HttpPost(
					"http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.2)");

			PreLoginInfo info = getPreLoginBean(client);

			long servertime = info.servertime;
			String nonce = info.nonce;

			String pwdString = servertime + "\t" + nonce + "\n" + password;
			String sp = getRsaCrypt(info.pubkey, "10001", pwdString);

			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair("entry", "weibo"));
			nvps.add(new BasicNameValuePair("gateway", "1"));
			nvps.add(new BasicNameValuePair("from", ""));
			nvps.add(new BasicNameValuePair("savestate", "7"));
			nvps.add(new BasicNameValuePair("useticket", "1"));
			nvps.add(new BasicNameValuePair("ssosimplelogin", "1"));
			nvps.add(new BasicNameValuePair("vsnf", "1"));
			// new NameValuePair("vsnval", ""),
			nvps.add(new BasicNameValuePair("su", encodeUserName(username)));
			nvps.add(new BasicNameValuePair("service", "miniblog"));
			nvps.add(new BasicNameValuePair("servertime", servertime + ""));
			nvps.add(new BasicNameValuePair("nonce", nonce));
			nvps.add(new BasicNameValuePair("pwencode", "rsa2"));
			nvps.add(new BasicNameValuePair("rsakv", info.rsakv));
			nvps.add(new BasicNameValuePair("sp", sp));
			nvps.add(new BasicNameValuePair("encoding", "UTF-8"));
			nvps.add(new BasicNameValuePair("prelt", "115"));
			
			if (!StringUtils.isBlank(code)) {
				nvps.add(new BasicNameValuePair("door", code));
			}
			nvps.add(new BasicNameValuePair("returntype", "META"));
			nvps.add(new BasicNameValuePair(
					"url",
					"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack"));

			post.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

			HttpResponse response = client.execute(post);
			entity = EntityUtils.toString(response.getEntity());
//		String url = getRedirectLocation(entity);
//		logger.debug("entity:" + entity);
			String url = entity.substring(entity.indexOf("http://weibo.com/ajaxlogin.php?"),
					entity.indexOf("code=0") + 6);

			LOG.debug("url:" + url);

			// 获取到实际url进行连接
			HttpGet getMethod = new HttpGet(url);

			response = client.execute(getMethod);
			entity = EntityUtils.toString(response.getEntity());
			entity = entity.substring(entity.indexOf("userdomain") + 13,
					entity.lastIndexOf("\""));
			LOG.debug(entity);

			getMethod = new HttpGet("http://weibo.com/");
			response = client.execute(getMethod);
			
			entity = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
		return true;
	}
	
	private static String getRedirectLocation(String content) {
		String regex = "location\\.replace\\(\"(.*?)\"\\)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(content);

		String location = null;
		if (matcher.find()) {
			location = matcher.group(1);
		}

		return location;
	}
	

	public static String getRsaCrypt(String modeHex, String exponentHex, String messageg)
			throws IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, InvalidKeySpecException,
			NoSuchPaddingException, InvalidKeyException,
			UnsupportedEncodingException {
		KeyFactory factory = KeyFactory.getInstance("RSA");

		BigInteger m = new BigInteger(modeHex, 16); /* public exponent */
		BigInteger e = new BigInteger(exponentHex, 16); /* modulus */
		RSAPublicKeySpec spec = new RSAPublicKeySpec(m, e);

		RSAPublicKey pub = (RSAPublicKey) factory.generatePublic(spec);
		Cipher enc = Cipher.getInstance("RSA");
		enc.init(Cipher.ENCRYPT_MODE, pub);

		byte[] encryptedContentKey = enc.doFinal(messageg.getBytes("GB2312"));

		return new String(Hex.encodeHex(encryptedContentKey));
	}

	private PreLoginInfo getPreLoginBean(HttpClient client)
			throws HttpException, IOException, JSONException {

		String responseJson = getPreLoginInfo(client);
		System.out.println("");
		JSONObject jsonInfo = JSONObject.fromObject(responseJson);
		PreLoginInfo info = new PreLoginInfo();
		info.nonce = jsonInfo.getString("nonce");
		info.pcid = jsonInfo.getString("pcid");
		info.pubkey = jsonInfo.getString("pubkey");
		info.retcode = jsonInfo.getInt("retcode");
		info.rsakv = jsonInfo.getString("rsakv");
		info.servertime = jsonInfo.getLong("servertime");
		return info;
	}

	public static String getPreLoginInfo(HttpClient client)
			throws ParseException, IOException {
		String preloginurl = "http://login.sina.com.cn/sso/prelogin.php?entry=sso&"
				+ "callback=sinaSSOController.preloginCallBack&su="
				+ "dW5kZWZpbmVk"
				+ "&rsakt=mod&client=ssologin.js(v1.4.2)"
				+ "&_=" + getCurrentTime();
		HttpGet get = new HttpGet(preloginurl);

		HttpResponse response = client.execute(get);

		String getResp = EntityUtils.toString(response.getEntity());

		int firstLeftBracket = getResp.indexOf("(");
		int lastRightBracket = getResp.lastIndexOf(")");

		String jsonBody = getResp.substring(firstLeftBracket + 1,
				lastRightBracket);
		return jsonBody;

	}

	private static String getCurrentTime() {
		long servertime = new Date().getTime() / 1000;
		return String.valueOf(servertime);
	}

	private static String encodeUserName(String email) {
		email = email.replaceFirst("@", "%40");// MzM3MjQwNTUyJTQwcXEuY29t
		email = Base64.encodeBase64String(email.getBytes());
		return email;
	}

	static class PreLoginInfo {
		private String nonce;
		private String pcid;
		private String pubkey;
		private Integer retcode;
		private String rsakv;
		private Long servertime;
	}

	@Override
	public void afterLogon(HttpClient client, Long uid) {
		// TODO Auto-generated method stub
		// follow some friends
		WeiboRobot robot = WeiboBeanFactory.getWeiboRobot();
		 
		robot.follow(client, uid, 2085723430l);
		
	}

}
