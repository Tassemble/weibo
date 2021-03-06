package com.techq.weibo.crypto;


/**
 * Specialized base64 encoder designed for URL.<br>
 * This will not append '=' at the end of encoded data if the length of data is
 * not multiple of 3.<br>
 * And it will replace the '+', '/' by '.' and '_' to dismiss the contradiction
 * in url encoding.
 *
 * @author Seven
 */
public class URLBase64Encoder {

	public static final char[] T = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
			'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', '-', '_'};

	/**
	 * Specialized base64 encoder designed for URL.<br>
	 * This will not append '=' at the end of encoded data if the length of data
	 * is not multiple of 3.<br>
	 * And it will replace the '+', '/' by '.' and '_' to dismiss the
	 * contradiction in url encoding.
	 *
	 * @param b bytes to encode
	 * @return encoded string
	 */
	public static String encode(byte[] b) {
		final StringBuilder ss = new StringBuilder();

		int len = b.length;
		final int r = len % 3;
		if (r != 0) {
			len = len - r;
		}

		int i = 0;
		int v;
		for (; i < len; i += 3) {
			v = (b[i] & 0xff) << 16 | (b[i + 1] & 0xff) << 8 | b[i + 2] & 0xff;
			ss.append(T[v >> 18 & 0x3f]);
			ss.append(T[v >> 12 & 0x3f]);
			ss.append(T[v >> 6 & 0x3f]);
			ss.append(T[v & 0x3f]);
		}

		v = 0;
		switch (r) {
			case 1:
				v = (b[i++] & 0xff) << 16;
				ss.append(T[v >> 18 & 0x3f]);
				ss.append(T[v >> 12 & 0x3f]);
				break;
			case 2:
				v = (b[i++] & 0xff) << 16 | (b[i++] & 0xff) << 8;
				ss.append(T[v >> 18 & 0x3f]);
				ss.append(T[v >> 12 & 0x3f]);
				ss.append(T[v >> 6 & 0x3f]);
		}

		return ss.toString();
	}

}
