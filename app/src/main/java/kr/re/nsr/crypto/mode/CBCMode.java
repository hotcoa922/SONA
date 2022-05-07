package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Ops.*;
import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeBlock;

public class CBCMode extends BlockCipherModeBlock {

	private byte[] iv;
	private byte[] feedback;

	public CBCMode(BlockCipher cipher) {
		super(cipher);
	}

	@Override
	public String getAlgorithmName() {
		return engine.getAlgorithmName() + "/CBC";
	}

	@Override
	public void init(Mode mode, byte[] mk, byte[] iv) {
		this.mode = mode;
		engine.init(mode, mk);
		this.iv = clone(iv);

		this.feedback = new byte[blocksize];
		reset();
	}

	@Override
	public void reset() {
		super.reset();
		System.arraycopy(iv, 0, feedback, 0, blocksize);
	}

	@Override
	protected int processBlock(byte[] in, int inOff, byte[] out, int outOff, int outlen) {
		if (outlen != blocksize) {
			throw new IllegalArgumentException("outlen should be " + blocksize + " in " + getAlgorithmName());
		}

		if (mode == Mode.ENCRYPT) {
			return encryptBlock(in, inOff, out, outOff);
		}

		return decryptBlock(in, inOff, out, outOff);
	}

	private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
		if ((inOff + blocksize) > in.length) {
			throw new IllegalStateException("input data too short");
		}

		XOR(feedback, 0, in, inOff, blocksize);

		engine.processBlock(feedback, 0, out, outOff);

		System.arraycopy(out, outOff, feedback, 0, blocksize);

		return blocksize;
	}

	private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
		if ((inOff + blocksize) > in.length) {
			throw new IllegalStateException("input data too short");
		}

		engine.processBlock(in, inOff, out, outOff);

		XOR(out, outOff, feedback, 0, blocksize);

		System.arraycopy(in, inOff, feedback, 0, blocksize);

		return blocksize;
	}

}
