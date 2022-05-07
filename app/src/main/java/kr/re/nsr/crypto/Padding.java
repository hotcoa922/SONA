package kr.re.nsr.crypto;

/**
 * 패딩 구현을 위한 인터페이스
 */
public abstract class Padding {

	protected int blocksize;

	public Padding(int blocksize) {
		this.blocksize = blocksize;
	}

	/**
	 * 패딩 추가
	 * 
	 * @param in
	 *            패딩을 추가할 메시지, 길이가 블록사이즈보다 같거나 작아야 함
	 */
	public abstract byte[] pad(byte[] in);

	/**
	 * 패딩 추가
	 * 
	 * @param in
	 *            패딩을 추가할 메시지가 포함된 배열, 배열 전체의 길이는 블록암호 블록사이즈와 같아야 함
	 * @param inOff
	 *            메시지 길이
	 */
	public abstract void pad(byte[] in, int inOff);

	/**
	 * 패딩 제거
	 * 
	 * @param in
	 *            패딩을 제거할 메시지
	 * @return 패딩이 제거된 메시지
	 */
	public abstract byte[] unpad(byte[] in);

	/**
	 * 패딩 길이 계산
	 * 
	 * @param in
	 *            패딩이 포함된 메시지
	 * @return 패딩의 길이
	 */
	public abstract int getPadCount(byte[] in);

}
