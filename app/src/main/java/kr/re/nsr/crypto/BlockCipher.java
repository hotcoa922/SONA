package kr.re.nsr.crypto;

/**
 * 블록암호 한블록 암/복호화 구현을 위한 인터페이스
 */
public abstract class BlockCipher {

	public enum Mode {
		/** 암호화 모드 */
		ENCRYPT,
		/** 복호화 모드 */
		DECRYPT
	}

	/**
	 * 초기화 함수
	 * 
	 * @param mode
	 *            {@link BlockCipher.Mode}
	 * @param mk
	 *            암호화 키
	 */
	public abstract void init(Mode mode, byte[] mk);

	/**
	 * 새로운 데이터를 처리하기 위해 init을 수행한 상태로 복원
	 */
	public abstract void reset();

	/**
	 * 암호화 알고리즘 이름을 리턴
	 * 
	 * @return 알고리즘 이름
	 */
	public abstract String getAlgorithmName();

	/**
	 * 암호화 알고리즘의 한 블록 크기를 리턴
	 * 
	 * @return 한 블록 크기
	 */
	public abstract int getBlockSize();

	/**
	 * 한블록 암호화
	 * 
	 * @param in
	 *            입력
	 * @param inOff
	 *            입력 시작 위치
	 * @param out
	 *            출력
	 * @param outOff
	 *            출력 시작 위치
	 * @return 처리한 데이터의 길이
	 */
	public abstract int processBlock(byte[] in, int inOff, byte[] out, int outOff);
}
