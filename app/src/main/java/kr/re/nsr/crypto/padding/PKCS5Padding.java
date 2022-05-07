package kr.re.nsr.crypto.padding;

import java.util.Arrays;

import kr.re.nsr.crypto.Padding;

public class PKCS5Padding extends Padding {

	public PKCS5Padding(int blocksize) {
		super(blocksize);
	}

	@Override
	public byte[] pad(byte[] in) {
		if (in == null) {
			throw new NullPointerException();
		}

		if (in.length < 0 || in.length > blocksize) {
			throw new IllegalStateException("input should be shorter than blocksize");
		}

		byte[] out = new byte[blocksize];
		System.arraycopy(in, 0, out, 0, in.length);
		pad(out, in.length);
		return out;
	}

	@Override
	public void pad(byte[] in, int inOff) {
		if (in == null) {
			throw new NullPointerException();
		}

		if (in.length < inOff) {
			throw new IllegalArgumentException();
		}

		byte code = (byte) (in.length - inOff);
		Arrays.fill(in, inOff, in.length, code);
	}

	@Override
	public byte[] unpad(byte[] in) {
		if (in == null || in.length < 1) {
			throw new NullPointerException();
		}

		if (in.length % blocksize != 0) {
			throw new IllegalArgumentException("Bad padding");
		}

		int cnt = in.length - getPadCount(in);
		if (cnt == 0) {
			return null;
		}

		byte[] out = new byte[cnt];
		System.arraycopy(in, 0, out, 0, out.length);

		return out;
	}

	@Override
	public int getPadCount(byte[] in) {
		if (in == null || in.length < 1) {
			throw new NullPointerException();
		}

		if (in.length % blocksize != 0) {
			throw new IllegalArgumentException("Bad padding");
		}

		int count = in[in.length - 1] & 0xff;

		boolean isBadPadding = false;
		int lower_bound = in.length - count;
		for (int i = in.length - 1; i > lower_bound; --i) {
			if (in[i] != count) {
				isBadPadding = true;
			}
		}

		if (isBadPadding) {
			throw new IllegalStateException("Bad Padding");
		}

		return count;
	}

}
