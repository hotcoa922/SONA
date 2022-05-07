package kr.re.nsr.crypto.mode;

import static kr.re.nsr.crypto.util.Ops.*;
import static kr.re.nsr.crypto.util.Pack.*;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher;
import kr.re.nsr.crypto.BlockCipher.Mode;
import kr.re.nsr.crypto.BlockCipherModeAE;

public class GCMMode extends BlockCipherModeAE {

	private static final int MAX_TAGLEN = 16;

	//@formatter:off
	private static final byte REDUCTION[][] = {
		{(byte)0x00, (byte)0x00}, {(byte)0x01, (byte)0xc2}, {(byte)0x03, (byte)0x84}, {(byte)0x02, (byte)0x46}, {(byte)0x07, (byte)0x08}, {(byte)0x06, (byte)0xca}, {(byte)0x04, (byte)0x8c}, {(byte)0x05, (byte)0x4e},
		{(byte)0x0e, (byte)0x10}, {(byte)0x0f, (byte)0xd2}, {(byte)0x0d, (byte)0x94}, {(byte)0x0c, (byte)0x56}, {(byte)0x09, (byte)0x18}, {(byte)0x08, (byte)0xda}, {(byte)0x0a, (byte)0x9c}, {(byte)0x0b, (byte)0x5e},
		{(byte)0x1c, (byte)0x20}, {(byte)0x1d, (byte)0xe2}, {(byte)0x1f, (byte)0xa4}, {(byte)0x1e, (byte)0x66}, {(byte)0x1b, (byte)0x28}, {(byte)0x1a, (byte)0xea}, {(byte)0x18, (byte)0xac}, {(byte)0x19, (byte)0x6e},
		{(byte)0x12, (byte)0x30}, {(byte)0x13, (byte)0xf2}, {(byte)0x11, (byte)0xb4}, {(byte)0x10, (byte)0x76}, {(byte)0x15, (byte)0x38}, {(byte)0x14, (byte)0xfa}, {(byte)0x16, (byte)0xbc}, {(byte)0x17, (byte)0x7e},
		{(byte)0x38, (byte)0x40}, {(byte)0x39, (byte)0x82}, {(byte)0x3b, (byte)0xc4}, {(byte)0x3a, (byte)0x06}, {(byte)0x3f, (byte)0x48}, {(byte)0x3e, (byte)0x8a}, {(byte)0x3c, (byte)0xcc}, {(byte)0x3d, (byte)0x0e},
		{(byte)0x36, (byte)0x50}, {(byte)0x37, (byte)0x92}, {(byte)0x35, (byte)0xd4}, {(byte)0x34, (byte)0x16}, {(byte)0x31, (byte)0x58}, {(byte)0x30, (byte)0x9a}, {(byte)0x32, (byte)0xdc}, {(byte)0x33, (byte)0x1e},
		{(byte)0x24, (byte)0x60}, {(byte)0x25, (byte)0xa2}, {(byte)0x27, (byte)0xe4}, {(byte)0x26, (byte)0x26}, {(byte)0x23, (byte)0x68}, {(byte)0x22, (byte)0xaa}, {(byte)0x20, (byte)0xec}, {(byte)0x21, (byte)0x2e},
		{(byte)0x2a, (byte)0x70}, {(byte)0x2b, (byte)0xb2}, {(byte)0x29, (byte)0xf4}, {(byte)0x28, (byte)0x36}, {(byte)0x2d, (byte)0x78}, {(byte)0x2c, (byte)0xba}, {(byte)0x2e, (byte)0xfc}, {(byte)0x2f, (byte)0x3e},
		{(byte)0x70, (byte)0x80}, {(byte)0x71, (byte)0x42}, {(byte)0x73, (byte)0x04}, {(byte)0x72, (byte)0xc6}, {(byte)0x77, (byte)0x88}, {(byte)0x76, (byte)0x4a}, {(byte)0x74, (byte)0x0c}, {(byte)0x75, (byte)0xce},
		{(byte)0x7e, (byte)0x90}, {(byte)0x7f, (byte)0x52}, {(byte)0x7d, (byte)0x14}, {(byte)0x7c, (byte)0xd6}, {(byte)0x79, (byte)0x98}, {(byte)0x78, (byte)0x5a}, {(byte)0x7a, (byte)0x1c}, {(byte)0x7b, (byte)0xde},
		{(byte)0x6c, (byte)0xa0}, {(byte)0x6d, (byte)0x62}, {(byte)0x6f, (byte)0x24}, {(byte)0x6e, (byte)0xe6}, {(byte)0x6b, (byte)0xa8}, {(byte)0x6a, (byte)0x6a}, {(byte)0x68, (byte)0x2c}, {(byte)0x69, (byte)0xee},
		{(byte)0x62, (byte)0xb0}, {(byte)0x63, (byte)0x72}, {(byte)0x61, (byte)0x34}, {(byte)0x60, (byte)0xf6}, {(byte)0x65, (byte)0xb8}, {(byte)0x64, (byte)0x7a}, {(byte)0x66, (byte)0x3c}, {(byte)0x67, (byte)0xfe},
		{(byte)0x48, (byte)0xc0}, {(byte)0x49, (byte)0x02}, {(byte)0x4b, (byte)0x44}, {(byte)0x4a, (byte)0x86}, {(byte)0x4f, (byte)0xc8}, {(byte)0x4e, (byte)0x0a}, {(byte)0x4c, (byte)0x4c}, {(byte)0x4d, (byte)0x8e},
		{(byte)0x46, (byte)0xd0}, {(byte)0x47, (byte)0x12}, {(byte)0x45, (byte)0x54}, {(byte)0x44, (byte)0x96}, {(byte)0x41, (byte)0xd8}, {(byte)0x40, (byte)0x1a}, {(byte)0x42, (byte)0x5c}, {(byte)0x43, (byte)0x9e},
		{(byte)0x54, (byte)0xe0}, {(byte)0x55, (byte)0x22}, {(byte)0x57, (byte)0x64}, {(byte)0x56, (byte)0xa6}, {(byte)0x53, (byte)0xe8}, {(byte)0x52, (byte)0x2a}, {(byte)0x50, (byte)0x6c}, {(byte)0x51, (byte)0xae},
		{(byte)0x5a, (byte)0xf0}, {(byte)0x5b, (byte)0x32}, {(byte)0x59, (byte)0x74}, {(byte)0x58, (byte)0xb6}, {(byte)0x5d, (byte)0xf8}, {(byte)0x5c, (byte)0x3a}, {(byte)0x5e, (byte)0x7c}, {(byte)0x5f, (byte)0xbe},
		{(byte)0xe1, (byte)0x00}, {(byte)0xe0, (byte)0xc2}, {(byte)0xe2, (byte)0x84}, {(byte)0xe3, (byte)0x46}, {(byte)0xe6, (byte)0x08}, {(byte)0xe7, (byte)0xca}, {(byte)0xe5, (byte)0x8c}, {(byte)0xe4, (byte)0x4e},
		{(byte)0xef, (byte)0x10}, {(byte)0xee, (byte)0xd2}, {(byte)0xec, (byte)0x94}, {(byte)0xed, (byte)0x56}, {(byte)0xe8, (byte)0x18}, {(byte)0xe9, (byte)0xda}, {(byte)0xeb, (byte)0x9c}, {(byte)0xea, (byte)0x5e},
		{(byte)0xfd, (byte)0x20}, {(byte)0xfc, (byte)0xe2}, {(byte)0xfe, (byte)0xa4}, {(byte)0xff, (byte)0x66}, {(byte)0xfa, (byte)0x28}, {(byte)0xfb, (byte)0xea}, {(byte)0xf9, (byte)0xac}, {(byte)0xf8, (byte)0x6e},
		{(byte)0xf3, (byte)0x30}, {(byte)0xf2, (byte)0xf2}, {(byte)0xf0, (byte)0xb4}, {(byte)0xf1, (byte)0x76}, {(byte)0xf4, (byte)0x38}, {(byte)0xf5, (byte)0xfa}, {(byte)0xf7, (byte)0xbc}, {(byte)0xf6, (byte)0x7e},
		{(byte)0xd9, (byte)0x40}, {(byte)0xd8, (byte)0x82}, {(byte)0xda, (byte)0xc4}, {(byte)0xdb, (byte)0x06}, {(byte)0xde, (byte)0x48}, {(byte)0xdf, (byte)0x8a}, {(byte)0xdd, (byte)0xcc}, {(byte)0xdc, (byte)0x0e},
		{(byte)0xd7, (byte)0x50}, {(byte)0xd6, (byte)0x92}, {(byte)0xd4, (byte)0xd4}, {(byte)0xd5, (byte)0x16}, {(byte)0xd0, (byte)0x58}, {(byte)0xd1, (byte)0x9a}, {(byte)0xd3, (byte)0xdc}, {(byte)0xd2, (byte)0x1e},
		{(byte)0xc5, (byte)0x60}, {(byte)0xc4, (byte)0xa2}, {(byte)0xc6, (byte)0xe4}, {(byte)0xc7, (byte)0x26}, {(byte)0xc2, (byte)0x68}, {(byte)0xc3, (byte)0xaa}, {(byte)0xc1, (byte)0xec}, {(byte)0xc0, (byte)0x2e},
		{(byte)0xcb, (byte)0x70}, {(byte)0xca, (byte)0xb2}, {(byte)0xc8, (byte)0xf4}, {(byte)0xc9, (byte)0x36}, {(byte)0xcc, (byte)0x78}, {(byte)0xcd, (byte)0xba}, {(byte)0xcf, (byte)0xfc}, {(byte)0xce, (byte)0x3e},
		{(byte)0x91, (byte)0x80}, {(byte)0x90, (byte)0x42}, {(byte)0x92, (byte)0x04}, {(byte)0x93, (byte)0xc6}, {(byte)0x96, (byte)0x88}, {(byte)0x97, (byte)0x4a}, {(byte)0x95, (byte)0x0c}, {(byte)0x94, (byte)0xce},
		{(byte)0x9f, (byte)0x90}, {(byte)0x9e, (byte)0x52}, {(byte)0x9c, (byte)0x14}, {(byte)0x9d, (byte)0xd6}, {(byte)0x98, (byte)0x98}, {(byte)0x99, (byte)0x5a}, {(byte)0x9b, (byte)0x1c}, {(byte)0x9a, (byte)0xde},
		{(byte)0x8d, (byte)0xa0}, {(byte)0x8c, (byte)0x62}, {(byte)0x8e, (byte)0x24}, {(byte)0x8f, (byte)0xe6}, {(byte)0x8a, (byte)0xa8}, {(byte)0x8b, (byte)0x6a}, {(byte)0x89, (byte)0x2c}, {(byte)0x88, (byte)0xee},
		{(byte)0x83, (byte)0xb0}, {(byte)0x82, (byte)0x72}, {(byte)0x80, (byte)0x34}, {(byte)0x81, (byte)0xf6}, {(byte)0x84, (byte)0xb8}, {(byte)0x85, (byte)0x7a}, {(byte)0x87, (byte)0x3c}, {(byte)0x86, (byte)0xfe},
		{(byte)0xa9, (byte)0xc0}, {(byte)0xa8, (byte)0x02}, {(byte)0xaa, (byte)0x44}, {(byte)0xab, (byte)0x86}, {(byte)0xae, (byte)0xc8}, {(byte)0xaf, (byte)0x0a}, {(byte)0xad, (byte)0x4c}, {(byte)0xac, (byte)0x8e},
		{(byte)0xa7, (byte)0xd0}, {(byte)0xa6, (byte)0x12}, {(byte)0xa4, (byte)0x54}, {(byte)0xa5, (byte)0x96}, {(byte)0xa0, (byte)0xd8}, {(byte)0xa1, (byte)0x1a}, {(byte)0xa3, (byte)0x5c}, {(byte)0xa2, (byte)0x9e},
		{(byte)0xb5, (byte)0xe0}, {(byte)0xb4, (byte)0x22}, {(byte)0xb6, (byte)0x64}, {(byte)0xb7, (byte)0xa6}, {(byte)0xb2, (byte)0xe8}, {(byte)0xb3, (byte)0x2a}, {(byte)0xb1, (byte)0x6c}, {(byte)0xb0, (byte)0xae},
		{(byte)0xbb, (byte)0xf0}, {(byte)0xba, (byte)0x32}, {(byte)0xb8, (byte)0x74}, {(byte)0xb9, (byte)0xb6}, {(byte)0xbc, (byte)0xf8}, {(byte)0xbd, (byte)0x3a}, {(byte)0xbf, (byte)0x7c}, {(byte)0xbe, (byte)0xbe},
	};
	//@formatter:on

	private byte[] initialCtr;
	private byte[] nonce;
	private byte[] tag;

	private byte[][] hTable;
	private byte[] block;
	private byte[] macBlock;
	private byte[] aadBlock;
	private byte[] hashBlock;
	private byte[] mulBlock;
	private byte[] inBuffer;

	private int blockOff;
	private int aadOff;
	private int aadlen;
	private int msglen;

	private ByteArrayOutputStream baos;

	public GCMMode(BlockCipher cipher) {
		super(cipher);

		block = new byte[blocksize];
		nonce = new byte[blocksize];

		hashBlock = new byte[blocksize];
		macBlock = new byte[blocksize];
		aadBlock = new byte[blocksize];
		mulBlock = new byte[blocksize];
		taglen = blocksize;
		msglen = 0;
	}

	@Override
	public void init(Mode mode, byte[] mk, byte[] nonce, int taglen) {
		this.mode = mode;
		engine.init(Mode.ENCRYPT, mk);

		if (mode == Mode.ENCRYPT) {
			inBuffer = new byte[blocksize];

		} else {
			inBuffer = new byte[blocksize + taglen];
		}

		reset();

		engine.processBlock(block, 0, block, 0);
		init_8bit_table();
		
		setNonce(nonce);
		setTaglen(taglen);

		baos = mode == Mode.ENCRYPT ? null : new ByteArrayOutputStream();
	}

	public void reset() {
		blockOff = 0;
		aadOff = 0;
		msglen = 0;
		aadlen = 0;

		Arrays.fill(block, (byte) 0x00);
		Arrays.fill(nonce, (byte) 0x00);

		Arrays.fill(hashBlock, (byte) 0x00);
		Arrays.fill(macBlock, (byte) 0x00);
		Arrays.fill(aadBlock, (byte) 0x00);
		Arrays.fill(mulBlock, (byte) 0x00);
		Arrays.fill(inBuffer, (byte) 0x00);

		if (baos != null) {
			baos.reset();
		}
	}

	private void setNonce(byte[] nonce) {
		if (nonce == null) {
			throw new NullPointerException("Nonce should not be null");
		}

		if (nonce.length < 1) {
			throw new IllegalArgumentException("the length of nonce should be larger than or equal to 1");
		}

		if (nonce.length == 12) {
			System.arraycopy(nonce, 0, this.nonce, 0, nonce.length);
			this.nonce[blocksize - 1] = 1;

		} else {
			ghash(this.nonce, nonce, nonce.length);
			byte[] X = new byte[blocksize];
			longToBigEndian((long) nonce.length * 8, X, 8);
			ghash(this.nonce, X, blocksize);
		}

		initialCtr = this.nonce.clone();
	}

	private void setTaglen(int taglen) {
		if (taglen < 0 || taglen > MAX_TAGLEN) {
			throw new IllegalArgumentException("length of tag should be 0~16 bytes");
		}

		this.taglen = taglen;
		tag = new byte[taglen];
	}

	@Override
	public int getOutputSize(int len) {
		int outSize = len + blockOff;
		if (mode == Mode.ENCRYPT) {
			return outSize + taglen;
		}

		return outSize < taglen ? 0 : outSize - taglen;
	}

	public int getUpdateOutputSize(int len) {
		int outSize = len + blockOff;

		if (mode == Mode.DECRYPT) {
			if (outSize < taglen) {
				return 0;
			}
			outSize -= taglen;
		}

		return outSize & 0xfffffff0;

	}

	@Override
	public void updateAAD(byte[] in) {
		if (in == null || in.length == 0) {
			return;
		}

		int len = in.length;
		int gap = aadBlock.length - aadOff;
		int inOff = 0;

		if (len > gap) {
			System.arraycopy(in, inOff, aadBlock, aadOff, gap);
			this.ghash(macBlock, aadBlock, blocksize);

			aadOff = 0;
			len -= gap;
			inOff += gap;
			aadlen += gap;

			while (len >= blocksize) {
				System.arraycopy(in, inOff, aadBlock, 0, blocksize);
				this.ghash(macBlock, aadBlock, blocksize);

				inOff += blocksize;
				len -= blocksize;
				aadlen += blocksize;
			}
		}

		if (len > 0) {
			System.arraycopy(in, inOff, aadBlock, aadOff, len);

			aadOff += len;
			aadlen += len;
		}
	}

	@Override
	public byte[] update(byte[] in) {
		if (aadOff != 0) {
			this.ghash(macBlock, aadBlock, aadOff);
			aadOff = 0;
		}

		if (mode == Mode.ENCRYPT) {
			return updateEncrypt(in);

		} else {
			updateDecrypt(in);
		}

		return null;
	}

	@Override
	public byte[] doFinal() {
		byte[] out = new byte[getOutputSize(0)];

		int outOff = 0;

		int extra = blockOff;
		if (extra != 0) {
			if (mode == Mode.ENCRYPT) {
				encryptBlock(out, outOff, extra);
				outOff += extra;

			} else {
				if (extra < taglen) {
					throw new IllegalArgumentException("data too short");
				}
				extra -= taglen;

				if (extra > 0) {
					decryptBlock(out, outOff, extra);

					try {
						baos.write(out, outOff, extra);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.arraycopy(inBuffer, extra, tag, 0, taglen);
			}
		}

		if (mode == Mode.DECRYPT) {
			msglen -= taglen;
		}

		Arrays.fill(block, (byte) 0x00);
		intToBigEndian(aadlen *= 8, block, 4);
		intToBigEndian(msglen *= 8, block, 12);
		ghash(macBlock, block, blocksize);

		engine.processBlock(initialCtr, 0, block, 0);
		XOR(macBlock, block);

		if (mode == Mode.ENCRYPT) {
			System.arraycopy(macBlock, 0, out, outOff, taglen);

		} else {
			macBlock = Arrays.copyOf(macBlock, taglen);
			if (Arrays.equals(macBlock, tag) == false && out != null) {
				baos.reset();
				out = null;

			} else {
				out = baos.toByteArray();
			}
		}

		return out;
	}

	private byte[] updateEncrypt(byte[] in) {
		int len = in.length;
		int gap = blocksize - blockOff;
		int inOff = 0;
		int outOff = 0;

		byte[] out = new byte[getUpdateOutputSize(len)];

		if (len >= gap) {
			System.arraycopy(in, inOff, inBuffer, blockOff, gap);
			encryptBlock(out, outOff, blocksize);

			len -= gap;
			inOff += gap;
			outOff += gap;

			msglen += gap;
			blockOff = 0;

			while (len >= blocksize) {
				System.arraycopy(in, inOff, inBuffer, 0, blocksize);
				encryptBlock(out, outOff, blocksize);

				len -= blocksize;
				inOff += blocksize;
				outOff += blocksize;

				msglen += blocksize;
			}
		}

		if (len > 0) {
			System.arraycopy(in, inOff, inBuffer, 0, len);

			msglen += len;
			blockOff += len;
		}

		return out;
	}

	private void updateDecrypt(byte[] in) {
		int len = in.length;

		int gap = inBuffer.length - blockOff;
		int inOff = 0;
		int outOff = 0;

		byte[] out = new byte[getUpdateOutputSize(len)];

		if (len >= gap) {
			System.arraycopy(in, inOff, inBuffer, blockOff, gap);
			decryptBlock(out, outOff, blocksize);
			System.arraycopy(inBuffer, blocksize, inBuffer, 0, taglen);

			len -= gap;
			inOff += gap;
			outOff += blocksize;

			msglen += gap;
			blockOff = taglen;

			while (len >= blocksize) {
				System.arraycopy(in, inOff, inBuffer, blockOff, blocksize);
				decryptBlock(out, outOff, blocksize);
				System.arraycopy(inBuffer, blocksize, inBuffer, 0, taglen);

				len -= blocksize;
				inOff += blocksize;
				outOff += blocksize;

				msglen += blocksize;
			}
		}

		if (len > 0) {
			System.arraycopy(in, inOff, inBuffer, blockOff, len);

			msglen += len;
			blockOff += len;
		}

		try {
			baos.write(out);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int encryptBlock(byte[] out, int offset, int len) {

		increase_counter(nonce);
		engine.processBlock(nonce, 0, block, 0);

		XOR(block, inBuffer);
		System.arraycopy(block, 0, out, offset, len);

		ghash(macBlock, block, len);

		return len;
	}

	private int decryptBlock(byte[] out, int outOff, int len) {

		System.arraycopy(inBuffer, 0, block, 0, len);
		ghash(macBlock, block, len);

		increase_counter(nonce);
		engine.processBlock(nonce, 0, block, 0);

		XOR(out, outOff, block, 0, inBuffer, 0, len);

		return len;
	}

	private void init_8bit_table() {
		hTable = new byte[256][16];
		byte[] temp = new byte[blocksize];

		System.arraycopy(block, 0, hTable[0x80], 0, block.length);
		System.arraycopy(block, 0, temp, 0, block.length);

		for (int j = 0x40; j >= 1; j >>= 1) {
			shiftRight(temp, 1);

			if ((this.hTable[j << 1][15] & 1) != 0) {
				temp[0] ^= 0xe1;
			}

			System.arraycopy(temp, 0, this.hTable[j], 0, 16);
		}

		for (int j = 2; j < 256; j <<= 1) {
			for (int k = 1; k < j; k++) {
				XOR(hTable[j + k], hTable[j], hTable[k]);
			}
		}
	}

	private void increase_counter(byte ctr[]) {

		for (int i = 15; i >= 12; --i) {
			if (++ctr[i] != 0) {
				return;
			}
		}
	}

	private void ghash(byte r[], byte data[], int data_len) {

		System.arraycopy(r, 0, hashBlock, 0, blocksize);

		int pos = 0;
		int len = data_len;
		for (; len >= blocksize; pos += blocksize, len -= blocksize) {
			XOR(hashBlock, 0, data, pos, blocksize);
			gfmul(hashBlock, hashBlock);
		}

		if (len > 0) {
			XOR(hashBlock, 0, data, pos, len);
			gfmul(hashBlock, hashBlock);
		}

		System.arraycopy(hashBlock, 0, r, 0, blocksize);
	}

	private void gfmul(byte r[], byte x[]) {
		Arrays.fill(mulBlock, (byte) 0x00);

		int rowIdx = 0;
		for (rowIdx = 15; rowIdx > 0; --rowIdx) {
			int colIdx = 0;

			for (colIdx = 0; colIdx < 16; ++colIdx) {
				mulBlock[colIdx] ^= hTable[(int) (x[rowIdx] & 0xff)][colIdx];
			}

			int mask = (int) (mulBlock[15] & 0xff);
			for (colIdx = 15; colIdx > 0; --colIdx) {
				mulBlock[colIdx] = mulBlock[colIdx - 1];
			}
			mulBlock[0] = 0;

			mulBlock[0] ^= REDUCTION[mask][0];
			mulBlock[1] ^= REDUCTION[mask][1];
		}
		rowIdx = (int) (x[0] & 0xff);

		XOR(r, mulBlock, hTable[rowIdx]);
	}

}
