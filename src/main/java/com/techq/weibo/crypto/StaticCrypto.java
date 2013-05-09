package com.techq.weibo.crypto;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicLong;

import com.techq.weibo.meta.User;

/**
 * 
 * multi-threads safe
 * @author chq
 * 
 */
public class StaticCrypto {

	public static AESEngine encEngine = null;
	public static AESEngine decEngine = null;

	public static String encrypt(String str)
			throws UnsupportedEncodingException {

		if (encEngine == null) {
			encEngine = getEncEngineInstance();
		}

		byte[] b = str.getBytes("utf-8");

		byte[] encrypted = new byte[b.length + encEngine.getBlockSize()];

		int n = 0;
		synchronized (encEngine) {
			n = encEngine.crypt(b, 0, b.length, encrypted, 0);
		}

		if (n < encrypted.length) {
			final byte[] copy = new byte[n];
			System.arraycopy(encrypted, 0, copy, 0, n);
			encrypted = copy;
		}

		return URLBase64Encoder.encode(encrypted).trim();
	}

	private static AESEngine getDecEngineInstance() {
		// TODO Auto-generated method stub
		synchronized (StaticCrypto.class) {
			if (decEngine == null) {
				decEngine = new AESEngine();
				decEngine.init(false, FS_URL);
			}
			return decEngine;
		}

	}

	private static AESEngine getEncEngineInstance() {
		// TODO Auto-generated method stub
		synchronized (StaticCrypto.class) {
			if (encEngine == null) {
				encEngine = new AESEngine();
				encEngine.init(true, FS_URL);
			}
			return encEngine;
		}
	}

	public static String decrypt(String str)
			throws UnsupportedEncodingException {

		if (decEngine == null)
			decEngine = getDecEngineInstance();

		byte[] b = URLBase64Decoder.decode(str);
		byte[] decrypted = new byte[b.length + decEngine.getBlockSize()];

		int n = 0;
		synchronized (decEngine) {
			n = decEngine.crypt(b, 0, b.length, decrypted, 0);
	}
		if (n < decrypted.length) {
			final byte[] copy = new byte[n];
			System.arraycopy(decrypted, 0, copy, 0, n);
			decrypted = copy;
		}

		return new String(decrypted, "utf-8").trim();
	}

	private StaticCrypto() {
		encEngine = new AESEngine();
		encEngine.init(true, FS_URL);

		decEngine = new AESEngine();
		decEngine.init(false, FS_URL);
	}

	private static final byte[] FS_URL = { (byte) 0x5a, (byte) 0x9f,
			(byte) 0x24, (byte) 0xd2, (byte) 0x9e, (byte) 0x9d, (byte) 0x35,
			(byte) 0x5f, (byte) 0xf9, (byte) 0xe4, (byte) 0x53, (byte) 0x39,
			(byte) 0xdc, (byte) 0xed, (byte) 0x46, (byte) 0xc6, (byte) 0x45,
			(byte) 0x15, (byte) 0x56, (byte) 0xb4, (byte) 0x0f, (byte) 0x25,
			(byte) 0x71, (byte) 0xca, (byte) 0x84, (byte) 0xc7, (byte) 0xd8,
			(byte) 0x56, (byte) 0xc6, (byte) 0x68, (byte) 0x9f, (byte) 0x7a };

	public static void main1(String[] args) {
		User user = new User();
		user.setAge(12);
		user.setArea("hangzhou");
		user.setEmail("chenhongqin@1267.com");
		user.setFansNumAddedToday(122L);
		user.setFansNumLimitToday(1111L);
		user.setFansNumWithUsingApp(1321L);
		user.setIds("123&123");
		user.setOnline(true);
		user.setPassword("haha");
		user.setSex(1);
		user.setUserId("213213");
		user.setWeiboUrl("http://weibo.com/124312341231");
		Crypto crypto = new Crypto();
		try {
			String encryStr = crypto.encrypt(user.toString());
			String encryStr1 = new String("CkwDrWvlK9rvn0BUtuhuv0jcJGxvoYxwOyeA8sbtmP_BsnFuwrXsOwZkQb3ufkAG8TFMqMLzPDqPkc738vEU9NaFfPzls5qKpkzG38GkACqtJzO5NlZ-IbEd48lKJYvSkUYz9ZQcY8UAzSWQsMKlRAiM18ddooxuriMc-UBmx-tL-hGwwM5U33BG_RC97-QkUzqRDyUvNuWgd52FWPV64n-SmQSepWjtQcC155teD9ySE6KimW7giRW3JNM_7J45AUJT"
					+ "mU46FA8-KtMQNsAOEOTHqjpjWGmRhTfbeNcjo_dvVD_iYB0UUiGJ68rWyx5c");
			if (encryStr.equals(encryStr1))
				System.out.println(true);
			System.out.println("encryStr:" + encryStr);
			String decryStr = crypto.decrypt(encryStr);
			System.out.println("decryStr:" + decryStr);
			// String base64str = URLBase64Encoder.en
			// code(encryStr.getBytes("UTF-8"));
			// System.out.println("base64:" + base64str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main2(String[] args) {
		
//		try {
//			String str = StaticCrypto
//			.decrypt("CkwDrWvlK9rvn0BUtuhuv0jcJGxvoYxwOyeA8sbtmP_BsnFuwrXsOwZkQb3ufkAG8TFMqMLzPDqPkc738vEU9NaFfPzls5qKpkzG38GkACqtJzO5NlZ-IbEd48lKJYvSkUYz9ZQcY8UAzSWQsMKlRAiM18ddooxuriMc-UBmx-tL-hGwwM5U33BG_RC97-QkUzqRDyUvNuWgd52FWPV64n-SmQSepWjtQcC155teD9ySE6KimW7giRW3JNM_7J45AUJT"
//					+ "mU46FA8-KtMQNsAOEOTHqjpjWGmRhTfbeNcjo_dvVD_iYB0UUiGJ68rWyx5c");
//			System.out.println(str);
//		} catch (UnsupportedEncodingException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		final String result = "age=12&area=hangzhou&email=chenhongqin@1267.com&fansNumAddedToday=122&fansNumLimitToday=1111&fansNumWithUsingApp=1321&ids=123&123&isOnline=true&password=haha&sex=1&userId=213213&username=null&weiboUrl=http://weibo.com/124312341231";
		final AtomicLong cnt = new AtomicLong(1);
		Thread thread1 = new Thread() {
			public void run() {
				while (true) {
				//	System.out.println(cnt.getAndIncrement());
					try {
						String ret = StaticCrypto
								.decrypt("CkwDrWvlK9rvn0BUtuhuv0jcJGxvoYxwOyeA8sbtmP_BsnFuwrXsOwZkQb3ufkAG8TFMqMLzPDqPkc738vEU9NaFfPzls5qKpkzG38GkACqtJzO5NlZ-IbEd48lKJYvSkUYz9ZQcY8UAzSWQsMKlRAiM18ddooxuriMc-UBmx-tL-hGwwM5U33BG_RC97-QkUzqRDyUvNuWgd52FWPV64n-SmQSepWjtQcC155teD9ySE6KimW7giRW3JNM_7J45AUJT"
										+ "mU46FA8-KtMQNsAOEOTHqjpjWGmRhTfbeNcjo_dvVD_iYB0UUiGJ68rWyx5c");
						if (!ret.equals(result)){
							System.out.println(false);
						}
					} catch (UnsupportedEncodingException e) {
						System.out.println("oop! exception");
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread2 = new Thread() {
			public void run() {
				while (true) {
				//	System.out.println(cnt.getAndIncrement());
					try {
						String str = StaticCrypto
								.decrypt("CkwDrWvlK9rvn0BUtuhuv0jcJGxvoYxwOyeA8sbtmP_BsnFuwrXsOwZkQb3ufkAG8TFMqMLzPDqPkc738vEU9NaFfPzls5qKpkzG38GkACqtJzO5NlZ-IbEd48lKJYvSkUYz9ZQcY8UAzSWQsMKlRAiM18ddooxuriMc-UBmx-tL-hGwwM5U33BG_RC97-QkUzqRDyUvNuWgd52FWPV64n-SmQSepWjtQcC155teD9ySE6KimW7giRW3JNM_7J45AUJT"
										+ "mU46FA8-KtMQNsAOEOTHqjpjWGmRhTfbeNcjo_dvVD_iYB0UUiGJ68rWyx5c");
						//System.out.println(str);
						if (!str.equals(result)){
							System.out.println(result + ":" + str);
							System.out.println(false);
						}
					} catch (UnsupportedEncodingException e) {
						System.out.println("oop! exception");
						e.printStackTrace();
					}
				}
			}
		};
		thread1.start();
		thread2.start();
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		String ret = "http://localhost:8080/rpc?param=" + StaticCrypto
		.encrypt("op=getuser&userid=1772403527");
		System.out.println(ret);
	}
	
}
