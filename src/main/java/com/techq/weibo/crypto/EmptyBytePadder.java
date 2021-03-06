package com.techq.weibo.crypto;


/**
 * Padding byte should be less or equal than 0x20, use 0x0 in this
 * implementation.
 * @author Seven
 */
public class EmptyBytePadder implements BlockPadder {

	public int pad(byte[] in, int inOff) {
		final int added = (in.length - inOff);

		while (inOff < in.length) {
			in[inOff] = (byte) 0;
			inOff++;
		}

		return added;
	}

	public int padCount(byte[] in) {
		int count = in.length;

		while (count > 0) {
			if (in[count - 1] != 0) {
				break;
			}

			count--;
		}

		return in.length - count;
	}
}
