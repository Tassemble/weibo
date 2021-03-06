package com.techq.weibo.crypto;


/**
 * Specialized base64 decoder designed for URL.<br>
 *
 * @author Seven
 */
public class URLBase64Decoder {

	private static final byte[] RT = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, 62, -1, -1, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1,
			-1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14,
			15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1,
			26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42,
			43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
			-1, -1, -1, -1, -1, -1};

	
	
	/**
	 * Specialized base64 decoder designed for URL.<br>
	 *
	 * @param s input string to decode
	 * @return byte data array
	 */
	public static byte[] decode(String s) {

		final char[] c = s.toCharArray();

		int len = c.length;
		final int r = len % 4;
		if (r != 0) {
			len = len - r;
		}

		final byte[] out = new byte[len / 4 * 3 + (r != 0 ? r - 1 : 0)];

		int v;
		int i = 0, j = 0;
		for (; i < len; i += 4) {

			v = RT[c[i] & 0xff] << 18 | RT[c[i + 1] & 0xff] << 12
					| RT[c[i + 2] & 0xff] << 6 | RT[c[i + 3] & 0xff];

			out[j++] = (byte) (v >>> 16);
			out[j++] = (byte) (v >>> 8);
			out[j++] = (byte) v;
		}

		v = 0;
		switch (r) {
			case 3:
				v = RT[c[i++] & 0xff] << 18 | RT[c[i++] & 0xff] << 12
						| RT[c[i++] & 0xff] << 6;
				out[j++] = (byte) (v >>> 16);
				out[j++] = (byte) (v >>> 8);
				break;
			case 2:
				v = RT[c[i++] & 0xff] << 18 | RT[c[i++] & 0xff] << 12;
				out[j++] = (byte) (v >>> 16);
				break;
			case 1: // error, should never happen
		}

		return out;
	}

	public static void main(String[] args) {
		final char[] c = {'M', 'T'};
		final byte[] b = URLBase64Decoder.decode(new String(c));
		System.out.println(new String(b));

		final String s0 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz几乎疯狂拉的肌肤!@#$%^&*()_+{}|<>?~";
		final String s = URLBase64Encoder.encode(s0.getBytes());
		System.out.println(s);
		System.out.println(s0);
		System.out.println(new String(URLBase64Decoder.decode(s)));
	}

}
