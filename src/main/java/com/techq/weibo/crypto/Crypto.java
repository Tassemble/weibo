package com.techq.weibo.crypto;


import java.io.UnsupportedEncodingException;

import com.techq.weibo.meta.User;

public class Crypto {

	public String encrypt(String str) throws UnsupportedEncodingException {

		byte[] b = str.getBytes("utf-8");

		byte[] encrypted = new byte[b.length + this.encEngine.getBlockSize()];

		final int n = this.encEngine.crypt(b, 0, b.length, encrypted, 0);
		if (n < encrypted.length) {
			final byte[] copy = new byte[n];
			System.arraycopy(encrypted, 0, copy, 0, n);
			encrypted = copy;
		}

		return URLBase64Encoder.encode(encrypted).trim();
	}

	public String decrypt(String str) throws UnsupportedEncodingException {
		byte[] b = URLBase64Decoder.decode(str);
		byte[] decrypted = new byte[b.length + this.decEngine.getBlockSize()];

		final int n = this.decEngine.crypt(b, 0, b.length, decrypted, 0);
		if (n < decrypted.length) {
			final byte[] copy = new byte[n];
			System.arraycopy(decrypted, 0, copy, 0, n);
			decrypted = copy;
		}

		return new String(decrypted, "utf-8").trim();
	}

	public Crypto() {
		this.encEngine = new AESEngine();
		encEngine.init(true, FS_URL);

		this.decEngine = new AESEngine();
		decEngine.init(false, FS_URL);
	}

	private AESEngine encEngine = null;
	private AESEngine decEngine = null;

	private static final byte[] FS_URL = {
			(byte) 0x5a, (byte) 0x9f, (byte) 0x24, (byte) 0xd2, (byte) 0x9e,
			(byte) 0x9d, (byte) 0x35, (byte) 0x5f, (byte) 0xf9, (byte) 0xe4,
			(byte) 0x53, (byte) 0x39, (byte) 0xdc, (byte) 0xed, (byte) 0x46,
			(byte) 0xc6, (byte) 0x45, (byte) 0x15, (byte) 0x56, (byte) 0xb4,
			(byte) 0x0f, (byte) 0x25, (byte) 0x71, (byte) 0xca, (byte) 0x84,
			(byte) 0xc7, (byte) 0xd8, (byte) 0x56, (byte) 0xc6, (byte) 0x68,
			(byte) 0x9f, (byte) 0x7a
	};

	
	public static void main(String[] args) {
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
			System.out.println("encryStr:" + encryStr);
			String decryStr = crypto.decrypt(encryStr);
			System.out.println("decryStr:" + decryStr);
		//	String base64str = URLBase64Encoder.en code(encryStr.getBytes("UTF-8"));
		//	System.out.println("base64:" + base64str);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
