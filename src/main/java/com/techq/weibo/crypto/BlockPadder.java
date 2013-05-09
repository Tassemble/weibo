package com.techq.weibo.crypto;


/**
 * @author Seven
 */
public interface BlockPadder {

	/**
	 * add the pad bytes to the passed in block, returning the number of bytes
	 * added.
	 */
	public int pad(byte[] in, int inOff);

	/**
	 * return the number of pad bytes present in the block.
	 */
	public int padCount(byte[] in);

}
