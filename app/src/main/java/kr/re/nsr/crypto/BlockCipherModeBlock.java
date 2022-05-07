package kr.re.nsr.crypto;

import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher.Mode;

public abstract class BlockCipherModeBlock extends BlockCipherModeImpl {

	protected Padding padding;

	public BlockCipherModeBlock(BlockCipher cipher) {
		super(cipher);
	}

	@Override
	public int getOutputSize(int len) {
		// TODO

		int size = ((len + bufferOffset) & blockmask) + blocksize;
		if (mode == Mode.ENCRYPT) {
			return padding != null ? size : len;
		}

		return len;
	}

	@Override
	public int getUpdateOutputSize(int len) {
		if (mode == Mode.DECRYPT && padding != null) {
			return (len + bufferOffset - blocksize) & blockmask;
		}

		return (len + bufferOffset) & blockmask;
	}

	@Override
	public void init(Mode mode, byte[] mk) {
		throw new IllegalStateException("This init method is not applicable to " + getAlgorithmName());
	}

	@Override
	public void init(Mode mode, byte[] mk, byte[] iv) {
		throw new IllegalStateException("This init method is not applicable to " + getAlgorithmName());
	}

	@Override
	public void reset() {
		bufferOffset = 0;
		Arrays.fill(buffer, (byte) 0);
	}

	@Override
	public void setPadding(Padding padding) {
		this.padding = padding;
	}

	@Override
	public byte[] update(byte[] msg) {
		if (padding != null && mode == Mode.DECRYPT) {
			return decryptWithPadding(msg);
		}

		if (msg == null) {
			return null;
		}

		int len = msg.length;
		int gap = buffer.length - bufferOffset;
		int inOff = 0;
		int outOff = 0;
		byte[] out = new byte[getUpdateOutputSize(len)];

		if (len >= gap) {
			System.arraycopy(msg, inOff, buffer, bufferOffset, gap);
			outOff += processBlock(buffer, 0, out, outOff);

			bufferOffset = 0;
			len -= gap;
			inOff += gap;

			while (len >= buffer.length) {
				outOff += processBlock(msg, inOff, out, outOff);
				len -= blocksize;
				inOff += blocksize;
			}
		}

		if (len > 0) {
			System.arraycopy(msg, inOff, buffer, bufferOffset, len);
			bufferOffset += len;
			len = 0;
		}

		return out;
	}

	@Override
	public byte[] doFinal() {
		if (padding != null) {
			return doFinalWithPadding();
		}

		if (bufferOffset == 0) {
			return null;

		} else if (bufferOffset != blocksize) {
			throw new IllegalStateException("Bad padding");
		}

		byte[] out = new byte[blocksize];
		processBlock(buffer, 0, out, 0, blocksize);

		return out;
	}

	/**
	 * 패딩 사용시 복호화 처리, 마지막 블록을 위해 데이터를 남겨둠
	 * 
	 * @param msg
	 * @return
	 */
	private byte[] decryptWithPadding(byte[] msg) {
		if (msg == null) {
			return null;
		}

		int len = msg.length;
		int gap = buffer.length - bufferOffset;
		int inOff = 0;
		int outOff = 0;
		byte[] out = new byte[getUpdateOutputSize(len)];

		if (len > gap) {
			System.arraycopy(msg, inOff, buffer, bufferOffset, gap);
			outOff += processBlock(buffer, 0, out, outOff);

			bufferOffset = 0;
			len -= gap;
			inOff += gap;

			while (len > buffer.length) {
				outOff += processBlock(msg, inOff, out, outOff);
				len -= blocksize;
				inOff += blocksize;
			}
		}

		if (len > 0) {
			System.arraycopy(msg, inOff, buffer, bufferOffset, len);
			bufferOffset += len;
			len = 0;
		}

		return out;
	}

	/**
	 * 패딩 사용시 마지막 블록 처리
	 * 
	 * @return
	 */
	private byte[] doFinalWithPadding() {
		byte[] out = null;

		if (mode == Mode.ENCRYPT) {
			padding.pad(buffer, bufferOffset);
			out = new byte[getOutputSize(0)];
			processBlock(buffer, 0, out, 0);

		} else {
			byte[] blk = new byte[blocksize];
			processBlock(buffer, 0, blk, 0);
			out = padding.unpad(blk);
		}

		return out;
	}
}
