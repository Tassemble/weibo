package com.techq.weibo.crypto;


/**
 * Block cipher engines are expected to conform to this interface.
 */
public interface BlockCipher {
	/**
	 * Initialise the cipher.
	 *
	 * @param forEncryption if true the cipher is initialised for encryption, if false for
	 *                      decryption.
	 * @param key           the key and other data required by the cipher.
	 * @throws IllegalArgumentException if the params argument is inappropriate.
	 */
	public void init(boolean forEncryption, byte[] key)
			throws IllegalArgumentException;

	/**
	 * Return the name of the algorithm the cipher implements.
	 *
	 * @return the name of the algorithm the cipher implements.
	 */
	public String getAlgorithmName();

	/**
	 * Return the block size for this cipher (in bytes).
	 *
	 * @return the block size for this cipher in bytes.
	 */
	public int getBlockSize();

	/**
	 * Process input bytes array in and write it to the out array.<br>
	 * this will append empty bytes at the end of input buffer if its length is
	 * not multiple of block size.
	 * @param in the array containing the input data.
	 * @param inOff offset into the in array the data starts at.
	 * @param length length of bytes to process
	 * @param out the array the output data will be copied into.
	 * @param outOff the offset into the out array the output will start at.
	 * @throws NullPointerException if input buffer or output buffer is null
	 * @throws IllegalArgumentException if input buffer offset or output buffer
	 *         off is less than 0
	 * @exception DataLengthException if there isn't enough data in in, or space
	 *            in out.
	 * @exception IllegalStateException if the cipher isn't initialised.
	 * @return the number of bytes processed and produced.
	 */
	public int crypt(byte[] in, int inOff, int length, byte[] out, int outOff);

}
