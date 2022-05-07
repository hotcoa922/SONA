package kr.re.nsr.crypto.util;

public class Hex {

	private Hex() {
		throw new AssertionError("Can't create an instance of class Hex");
	}

	public static final byte[] decodeHexString(String hexString) {
		if (hexString == null) {
			return null;
		}

		byte[] buf = new byte[hexString.length() / 2];

		for (int i = 0; i < buf.length; ++i) {
			buf[i] = (byte) (16 * decodeHexChar(hexString.charAt(i * 2)));
			buf[i] += decodeHexChar(hexString.charAt(i * 2 + 1));
		}

		return buf;
	}

	private static final byte decodeHexChar(char ch) {
		if (ch >= '0' && ch <= '9') {
			return (byte) (ch - '0');
		}

		if (ch >= 'a' && ch <= 'f') {
			return (byte) (ch - 'a' + 10);
		}

		if (ch >= 'A' && ch <= 'F') {
			return (byte) (ch - 'A' + 10);
		}

		return 0;
	}

	public static final byte[] toBytes(int value, int len) {
		byte[] buf = new byte[len];
		toBytes(value, buf, 0, len);
		return buf;
	}

	public static final void toBytes(long value, byte[] buf, int offset, int len) {
		if (len <= 0) {
			throw new IllegalArgumentException("len should be positive integer");
		}

		for (int i = offset + len - 1, shift = 0; i >= offset; --i, shift += 8) {
			buf[i] = (byte) ((value >>> shift) & 0xff);
		}
	}

	/**
	 * print the content of a byte buffer to the system output as a hex string
	 * 
	 * @param title
	 * @param buf
	 */
	public static final void printHex(String title, byte[] buf) {
		if (buf == null) {
			throw new NullPointerException("input array shoud not be null");
		}

		System.out.print(title + "(" + buf.length + ") ");
		System.out.println(toHexString(buf));
	}

	/**
	 * print the content of an int buffer to the system output as a hex string
	 * 
	 * @param title
	 * @param buf
	 */
	public static final void printHex(String title, int[] buf) {
		if (buf == null) {
			throw new NullPointerException("input array shoud not be null");
		}

		System.out.print(title + "(" + buf.length + ") ");
		System.out.println(toHexString(buf));
	}

	/**
	 * convert byte buffer to hex string
	 * 
	 * @param buf
	 * @return
	 */
	public static final String toHexString(byte[] buf) {
		if (buf == null) {
			return null;
		}

		return toHexString(buf, 0, buf.length, 0);
	}

	public static final String toHexString(byte[] buf, int indent) {
		if (buf == null) {
			throw null;
		}

		return toHexString(buf, 0, buf.length, indent);
	}

	public static final String toHexString(byte[] buf, int offset, int len, int indent) {
		if (buf == null) {
			return null;
		}

		if (buf.length < offset + len) {
			throw new IllegalArgumentException("buffer length is not enough");
		}

		StringBuilder sb = new StringBuilder();

		int index = 0;
		for (int i = offset; i < offset + len; ++i) {
			sb.append(String.format("%02x", buf[i]));
			++index;

			if (index != len && indent != 0 && (index % indent) == 0) {
				sb.append(" ");
			}
		}

		return sb.toString();
	}

	/**
	 * convert int buffer to hex string
	 * 
	 * @param buf
	 * @return
	 */
	public static final String toHexString(int[] buf) {
		if (buf == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();

		for (int ch : buf) {
			sb.append(String.format("%08x", ch));
			sb.append("  ");
		}

		return sb.toString();
	}

	public static final String toBitString(byte[] in) {
		if (in == null) {
			throw new NullPointerException("input array shoud not be null");
		}

		StringBuilder sb = new StringBuilder();
		for (byte i : in) {
			sb.append(toBitString(i));
		}

		return sb.toString();
	}

	public static final String toBitString(byte in) {
		StringBuilder sb = new StringBuilder();

		for (int i = 7; i >= 0; --i) {
			sb.append(in >>> i & 1);
		}

		return sb.toString();
	}

	public static final String toBitString(int[] in) {
		if (in == null) {
			throw new NullPointerException("input array shoud not be null");
		}

		StringBuilder sb = new StringBuilder();
		for (int i : in) {
			sb.append(toBitString(i));
		}

		return sb.toString();
	}

	public static final String toBitString(int in) {
		StringBuilder sb = new StringBuilder();

		for (int i = 31; i >= 0; --i) {
			sb.append(in >>> i & 1);
		}

		return sb.toString();
	}
}
