package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Hex.*;
import static kr.re.nsr.crypto.util.Ops.*;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeAE;

public class CCMMode extends BlockCipherModeAE {

	private byte[] ctr;
	private byte[] mac;
	private byte[] tag;
	private byte[] block;

	private ByteArrayOutputStream aadBytes;
	private ByteArrayOutputStream inputBytes;

	private int msglen;
	private int taglen;
	private int noncelen;

	public CCMMode(BlockCipher cipher) {
		super(cipher);

		ctr = new byte[blocksize];
		mac = new byte[blocksize];
		block = new byte[blocksize];
	}

	@Override
	public void init(Mode mode, byte[] mk, byte[] nonce, int taglen) {
		this.mode = mode;
		engine.init(Mode.ENCRYPT, mk);

		aadBytes = new ByteArrayOutputStream();
		inputBytes = new ByteArrayOutputStream();

		setTaglen(taglen);
		setNonce(nonce);
	}

	@Override
	public void updateAAD(byte[] aad) {
		if (aad == null || aad.length == 0) {
			return;
		}

		aadBytes.write(aad, 0, aad.length);
	}

	@Override
	public byte[] update(byte[] msg) {
		inputBytes.write(msg, 0, msg.length);
		return null;
	}

	@Override
	public byte[] doFinal() {
		close(aadBytes);
		close(inputBytes);

		if (aadBytes.size() > 0) {
			block[0] |= (byte) 0x40;
		}

		msglen = inputBytes.toByteArray().length;
		if (mode == Mode.DECRYPT) {
			msglen -= taglen;
		}

		toBytes(msglen, block, noncelen + 1, 15 - noncelen);
		engine.processBlock(block, 0, mac, 0);

		byte[] out;

		processAAD();
		if (mode == Mode.ENCRYPT) {
			out = new byte[msglen + taglen];
			encryptData(out, 0);

		} else {
			out = new byte[msglen];
			decryptData(out, 0);
		}

		resetCounter();
		engine.processBlock(ctr, 0, block, 0);

		if (mode == Mode.ENCRYPT) {
			XOR(mac, block);
			System.arraycopy(mac, 0, tag, 0, taglen);
			System.arraycopy(mac, 0, out, out.length - taglen, taglen);

		} else {
			mac = Arrays.copyOf(mac, taglen);
			if (Arrays.equals(tag, mac) == false) {
				Arrays.fill(out, (byte) 0);
			}
		}

		return out;
	}

	@Override
	public int getOutputSize(int len) {
		int outSize = len + bufOff;
		if (mode == Mode.ENCRYPT) {
			return outSize + taglen;
		}

		return outSize < taglen ? 0 : outSize - taglen;
	}

	private void setNonce(byte[] nonce) {
		if (nonce == null) {
			throw new NullPointerException("nonce is null");
		}

		noncelen = nonce.length;
		if (noncelen < 7 || noncelen > 13) {
			throw new IllegalArgumentException("length of nonce should be 7 ~ 13 bytes");
		}

		// init counter
		ctr[0] = (byte) (14 - noncelen);
		System.arraycopy(nonce, 0, ctr, 1, noncelen);

		// init b0
		int tagfield = (taglen - 2) / 2;
		block[0] = (byte) ((tagfield << 3) & 0xff);
		block[0] |= (byte) ((14 - noncelen) & 0xff);
		System.arraycopy(nonce, 0, block, 1, noncelen);
	}

	private void setTaglen(int taglen) {
		if (taglen < 4 || taglen > 16 || (taglen & 0x01) != 0) {
			throw new IllegalArgumentException("length of tag should be 4, 6, 8, 10, 12, 14, 16 bytes");
		}

		this.taglen = taglen;
		tag = new byte[taglen];
	}

	private void resetCounter() {
		Arrays.fill(ctr, noncelen + 1, ctr.length, (byte) 0);
	}

	private void increaseCounter() {
		int i = ctr.length - 1;
		while (++ctr[i] == 0) {
			--i;
			if (i < noncelen + 1) {
				throw new IllegalStateException("exceed maximum counter");
			}
		}
	}

	private void processAAD() {
		byte[] aad = aadBytes.toByteArray();

		Arrays.fill(block, (byte) 0);

		int alen = 0;
		if (aad.length < 0xff00) {
			alen = 2;
			toBytes(aad.length, block, 0, 2);

		} else {
			alen = 6;
			block[0] = (byte) 0xff;
			block[1] = (byte) 0xfe;
			toBytes(aad.length, block, 2, 4);
		}

		if (aad.length == 0) {
			return;
		}

		int i = 0;
		int remained = aad.length;
		int processed = remained > blocksize - alen ? blocksize - alen : aad.length;
		i += processed;
		remained -= processed;
		System.arraycopy(aad, 0, block, alen, processed);

		XOR(mac, block);
		engine.processBlock(mac, 0, mac, 0);

		while (remained > 0) {
			processed = remained >= blocksize ? blocksize : remained;
			XOR(mac, 0, mac, 0, aad, i, processed);
			engine.processBlock(mac, 0, mac, 0);

			i += processed;
			remained -= processed;
		}
	}

	private void encryptData(byte[] out, int offset) {
		int inIdx = 0;
		int remained = 0;
		int processed = 0;
		int outIdx = offset;

		byte[] in = inputBytes.toByteArray();

		remained = msglen;
		while (remained > 0) {
			processed = remained >= blocksize ? blocksize : remained;

			XOR(mac, 0, mac, 0, in, inIdx, processed);
			engine.processBlock(mac, 0, mac, 0);

			increaseCounter();
			engine.processBlock(ctr, 0, block, 0);
			XOR(out, outIdx, block, 0, in, inIdx, processed);

			inIdx += processed;
			outIdx += processed;
			remained -= processed;
		}
	}

	private void decryptData(byte[] out, int offset) {
		int i = 0;
		int remained = 0;
		int processed = 0;
		int outIdx = offset;

		byte[] in = inputBytes.toByteArray();

		System.arraycopy(in, msglen, tag, 0, taglen);
		engine.processBlock(ctr, 0, block, 0);
		XOR(tag, 0, block, 0, taglen);

		remained = msglen;
		while (remained > 0) {
			processed = remained >= blocksize ? blocksize : remained;

			increaseCounter();
			engine.processBlock(ctr, 0, block, 0);
			XOR(out, outIdx, block, 0, in, i, processed);

			XOR(mac, 0, mac, 0, out, outIdx, processed);
			engine.processBlock(mac, 0, mac, 0);

			i += processed;
			outIdx += processed;
			remained -= processed;
		}
	}

	private static void close(Closeable obj) {
		if (obj == null) {
			return;
		}

		try {
			obj.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
