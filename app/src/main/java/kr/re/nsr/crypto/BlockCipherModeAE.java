package kr.re.nsr.crypto;

import kr.re.nsr.crypto.BlockCipher.Mode;

public abstract class BlockCipherModeAE {

	protected Mode mode;
	protected BlockCipher engine;

	protected byte[] buffer;
	protected byte[] nonce;
	protected int bufOff;
	protected int blocksize;

	protected int taglen;

	public BlockCipherModeAE(BlockCipher cipher) {
		engine = cipher;
		blocksize = engine.getBlockSize();
		buffer = new byte[blocksize];
	}

	public abstract void init(Mode mode, byte[] mk, byte[] nonce, int taglen);

	public abstract void updateAAD(byte[] aad);

	public abstract byte[] update(byte[] msg);

	public abstract byte[] doFinal();

	public abstract int getOutputSize(int len);

	public byte[] doFinal(byte[] msg) {
		byte[] out = null;

		if (mode == Mode.ENCRYPT) {
			byte[] part1 = update(msg);
			byte[] part2 = doFinal();

			int len1 = part1 == null ? 0 : part1.length;
			int len2 = part2 == null ? 0 : part2.length;

			out = new byte[len1 + len2];
			if (part1 != null)
				System.arraycopy(part1, 0, out, 0, len1);

			if (part2 != null)
				System.arraycopy(part2, 0, out, len1, len2);

		} else {
			update(msg);
			out = doFinal();
		}

		return out;
	}
}
