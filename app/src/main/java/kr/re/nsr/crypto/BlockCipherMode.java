package kr.re.nsr.crypto;

/**
 * 블록암호 운용모드 구현을 위한 인터페이스
 */
public abstract class BlockCipherMode {

	/**
	 * 현재 객체가 구현하고 있는 알고리즘 이름을 리턴
	 * 
	 * @return 알고리즘 이름을 "블록암호/운용모드" 형태로 리턴
	 */
	public abstract String getAlgorithmName();

	/**
	 * 입력에 대한 출력 길이를 계산
	 * 
	 * @param len
	 *            입력 길이
	 * @return len 만큼의 입력을 처리하기 위해 필요한 출력 길이
	 */
	public abstract int getOutputSize(int len);

	/**
	 * 부분 업데이트를 위해 필요한 출력의 길이 계산, 주로 블록크기의 배수로 계산됨
	 * 
	 * @param len
	 *            입력 길이
	 * @return len 만큼의 입력을 처리하기 위해 필요한 중간 출력 길이
	 */
	public abstract int getUpdateOutputSize(int len);

	/**
	 * IV를 필요로 하지 않는 운용모드를 위한 초기화 함수
	 * 
	 * @param mode
	 *            {@link BlockCipher.Mode}
	 * @param mk
	 *            암호화 키
	 */
	public abstract void init(BlockCipher.Mode mode, byte[] mk);

	/**
	 * IV를 필요로 하는 운용모드를 위한 초기화 함수
	 * 
	 * @param mode
	 *            {@link BlockCipher.Mode}
	 * @param mk
	 *            암호화 키
	 * @param iv
	 *            초기화 벡터
	 */
	public abstract void init(BlockCipher.Mode mode, byte[] mk, byte[] iv);

	/**
	 * init을 완료한 상태로 변경, 새 메시지를 처리하기 위함
	 */
	public abstract void reset();

	/**
	 * PKCS7Padding 사용 여부 설정, 기본으로는 사용
	 * 
	 * @param padding
	 *            패딩 알고리즘 객체
	 */
	public abstract void setPadding(Padding padding);

	/**
	 * 온라인 모드를 위한 업데이트 함수
	 * 
	 * @param msg
	 *            처리할 메시지 일부
	 * @return 처리된 메시지 일부
	 */
	public abstract byte[] update(byte[] msg);

	/**
	 * 암/복호화 최종단계 수행
	 * 
	 * @param msg
	 *            평문/암호문의 마지막 부분
	 * @return 암호문 혹은 평문의 마지막 부분
	 */
	public abstract byte[] doFinal(byte[] msg);

	/**
	 * 암/복호화 최종단계 수행
	 * 
	 * @return 암호문 혹은 평문의 마지막 부분
	 */
	public abstract byte[] doFinal();

}
