package kr.re.nsr.crypto.engine;

import java.util.Arrays;

import kr.re.nsr.crypto.BlockCipher;

import static kr.re.nsr.crypto.util.Ops.*;

public class LeaEngine extends BlockCipher {

	private static final int BLOCKSIZE = 16;
	private static final int delta[] = { 0xc3efe9db, 0x44626b02, 0x79e27c8a, 0x78df30ec, 0x715ea49e, 0xc785da0a, 0xe04ef22a, 0xe5c40957 };

	private Mode mode;
	private int rounds;
	protected int[][] roundKeys;
	private int[] block;

	public LeaEngine() {
		block = new int[BLOCKSIZE / 4];
	}

	@Override
	public void init(Mode mode, byte[] mk) {
		this.mode = mode;
		generateRoundKeys(mk);
	}

	@Override
	public void reset() {
		Arrays.fill(block, 0);
	}

	@Override
	public String getAlgorithmName() {
		return "LEA";
	}

	@Override
	public int getBlockSize() {
		return BLOCKSIZE;
	}

	@Override
	public int processBlock(byte[] in, int inOff, byte[] out, int outOff) throws IllegalStateException {
		if (in == null || out == null) {
			throw new NullPointerException("in and out should not be null");
		}

		if (in.length - inOff < BLOCKSIZE) {
			throw new IllegalStateException("too short input data " + in.length + " " + inOff);
		}

		if (out.length - outOff < BLOCKSIZE) {
			throw new IllegalStateException("too short output buffer " + out.length + " / " + outOff);
		}

		if (mode == Mode.ENCRYPT) {
			return encryptBlock(in, inOff, out, outOff);
		}

		return decryptBlock(in, inOff, out, outOff);
	}

	private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {

		pack(in, inOff, block, 0, 16);

		for (int i = 0; i < this.rounds; ++i) {
			block[3] = ROR((block[2] ^ roundKeys[i][4]) + (block[3] ^ roundKeys[i][5]), 3);
			block[2] = ROR((block[1] ^ roundKeys[i][2]) + (block[2] ^ roundKeys[i][3]), 5);
			block[1] = ROL((block[0] ^ roundKeys[i][0]) + (block[1] ^ roundKeys[i][1]), 9);
			++i;

			block[0] = ROR((block[3] ^ roundKeys[i][4]) + (block[0] ^ roundKeys[i][5]), 3);
			block[3] = ROR((block[2] ^ roundKeys[i][2]) + (block[3] ^ roundKeys[i][3]), 5);
			block[2] = ROL((block[1] ^ roundKeys[i][0]) + (block[2] ^ roundKeys[i][1]), 9);

			++i;
			block[1] = ROR((block[0] ^ roundKeys[i][4]) + (block[1] ^ roundKeys[i][5]), 3);
			block[0] = ROR((block[3] ^ roundKeys[i][2]) + (block[0] ^ roundKeys[i][3]), 5);
			block[3] = ROL((block[2] ^ roundKeys[i][0]) + (block[3] ^ roundKeys[i][1]), 9);

			++i;
			block[2] = ROR((block[1] ^ roundKeys[i][4]) + (block[2] ^ roundKeys[i][5]), 3);
			block[1] = ROR((block[0] ^ roundKeys[i][2]) + (block[1] ^ roundKeys[i][3]), 5);
			block[0] = ROL((block[3] ^ roundKeys[i][0]) + (block[0] ^ roundKeys[i][1]), 9);
		}

		unpack(block, 0, out, outOff, 4);

		return BLOCKSIZE;
	}

	private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {

		pack(in, inOff, block, 0, 16);

		for (int i = this.rounds - 1; i >= 0; --i) {
			block[0] = (ROR(block[0], 9) - (block[3] ^ roundKeys[i][0])) ^ roundKeys[i][1];
			block[1] = (ROL(block[1], 5) - (block[0] ^ roundKeys[i][2])) ^ roundKeys[i][3];
			block[2] = (ROL(block[2], 3) - (block[1] ^ roundKeys[i][4])) ^ roundKeys[i][5];
			--i;

			block[3] = (ROR(block[3], 9) - (block[2] ^ roundKeys[i][0])) ^ roundKeys[i][1];
			block[0] = (ROL(block[0], 5) - (block[3] ^ roundKeys[i][2])) ^ roundKeys[i][3];
			block[1] = (ROL(block[1], 3) - (block[0] ^ roundKeys[i][4])) ^ roundKeys[i][5];
			--i;

			block[2] = (ROR(block[2], 9) - (block[1] ^ roundKeys[i][0])) ^ roundKeys[i][1];
			block[3] = (ROL(block[3], 5) - (block[2] ^ roundKeys[i][2])) ^ roundKeys[i][3];
			block[0] = (ROL(block[0], 3) - (block[3] ^ roundKeys[i][4])) ^ roundKeys[i][5];
			--i;

			block[1] = (ROR(block[1], 9) - (block[0] ^ roundKeys[i][0])) ^ roundKeys[i][1];
			block[2] = (ROL(block[2], 5) - (block[1] ^ roundKeys[i][2])) ^ roundKeys[i][3];
			block[3] = (ROL(block[3], 3) - (block[2] ^ roundKeys[i][4])) ^ roundKeys[i][5];
		}

		unpack(block, 0, out, outOff, 4);

		return BLOCKSIZE;
	}

	private void generateRoundKeys(byte[] mk) {
		if (mk == null || ((mk.length != 16) && (mk.length != 24) && (mk.length != 32))) {
			throw new IllegalArgumentException("Illegal key");
		}

		int[] T = new int[8];

		this.rounds = (mk.length >> 1) + 16;
		this.roundKeys = new int[this.rounds][6];

		pack(mk, 0, T, 0, 16);

		if (mk.length > 16) {
			pack(mk, 16, T, 4, 8);
		}

		if (mk.length > 24) {
			pack(mk, 24, T, 6, 8);
		}

		if (mk.length == 16) {
			for (int i = 0; i < 24; ++i) {
				int temp = ROL(delta[i & 3], i);

				this.roundKeys[i][0] = T[0] = ROL(T[0] + ROL(temp, 0), 1);
				this.roundKeys[i][1] = this.roundKeys[i][3] = this.roundKeys[i][5] = T[1] = ROL(T[1] + ROL(temp, 1), 3);
				this.roundKeys[i][2] = T[2] = ROL(T[2] + ROL(temp, 2), 6);
				this.roundKeys[i][4] = T[3] = ROL(T[3] + ROL(temp, 3), 11);
			}

		} else if (mk.length == 24) {
			for (int i = 0; i < 28; ++i) {
				int temp = ROL(delta[i % 6], i);

				this.roundKeys[i][0] = T[0] = ROL(T[0] + ROL(temp, 0), 1);
				this.roundKeys[i][1] = T[1] = ROL(T[1] + ROL(temp, 1), 3);
				this.roundKeys[i][2] = T[2] = ROL(T[2] + ROL(temp, 2), 6);
				this.roundKeys[i][3] = T[3] = ROL(T[3] + ROL(temp, 3), 11);
				this.roundKeys[i][4] = T[4] = ROL(T[4] + ROL(temp, 4), 13);
				this.roundKeys[i][5] = T[5] = ROL(T[5] + ROL(temp, 5), 17);
			}

		} else {
			for (int i = 0; i < 32; ++i) {
				int temp = ROL(delta[i & 7], i & 0x1f);

				this.roundKeys[i][0] = T[(6 * i + 0) & 7] = ROL(T[(6 * i + 0) & 7] + temp, 1);
				this.roundKeys[i][1] = T[(6 * i + 1) & 7] = ROL(T[(6 * i + 1) & 7] + ROL(temp, 1), 3);
				this.roundKeys[i][2] = T[(6 * i + 2) & 7] = ROL(T[(6 * i + 2) & 7] + ROL(temp, 2), 6);
				this.roundKeys[i][3] = T[(6 * i + 3) & 7] = ROL(T[(6 * i + 3) & 7] + ROL(temp, 3), 11);
				this.roundKeys[i][4] = T[(6 * i + 4) & 7] = ROL(T[(6 * i + 4) & 7] + ROL(temp, 4), 13);
				this.roundKeys[i][5] = T[(6 * i + 5) & 7] = ROL(T[(6 * i + 5) & 7] + ROL(temp, 5), 17);
			}
		}

	}

	// utilities
	private static final int ROL(int state, int num) {
		return (int) ((state << num) | (state >>> (32 - num)));
	}

	private static final int ROR(int state, int num) {
		return (int) ((state >>> num) | (state << (32 - num)));
	}

}
