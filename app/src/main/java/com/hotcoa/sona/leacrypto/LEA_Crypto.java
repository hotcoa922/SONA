
package com.hotcoa.sona.leacrypto;

import static java.lang.System.arraycopy;

import kr.re.nsr.crypto.*;
import kr.re.nsr.crypto.padding.PKCS5Padding;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.security.MessageDigest;
import java.util.Scanner;


import android.content.Context;
import android.provider.Settings;

public class LEA_Crypto {
    public byte[] toByteArray(String string) {
        // String to ByteArray
        byte[] bytes = new byte[string.length()];
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++)
        {
            bytes[i] = (byte)chars[i];
        }

        return bytes;
    }
    public String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    public byte[] PBKDF(String P) throws Exception {
        // PBKDF 클래스
        byte[] S = new byte[] {0x78, 0x57, (byte)0x8e, 0x5a, 0x5d, 0x63, (byte)0xcb, 0x06};
        int c = 1000;
        int dkLen = 16;

        Security.addProvider(new BouncyCastleProvider());

        MessageDigest md = MessageDigest.getInstance("SHA1", "BC");

        byte[] input = new byte[P.length() + S.length];

        System.arraycopy(toByteArray(P), 0, input, 0, P.length());
        System.arraycopy(S, 0, input, P.length(), S.length);

        md.update(input);
        for (int i = 0; i < c - 1; i++) {
            byte[] T = md.digest();
            md.update(T);
        }

        byte[] output = md.digest();
        byte[] result = new byte[16];

        arraycopy(output, 0, result, 0, dkLen);

        // 16바이트 크기의 bytearray 를 반환한다.
        return result;
    }

    /*
    public static void encode(String fileName, String en_fileName, byte[] keyBytes) throws Exception {
         LEA
        // 객체 생성
        BlockCipherMode cipher = new LEA.CBC();
        // 암호화
        cipher.init(Mode.ENCRYPT, key, iv);
        cipher.setPadding(new PKCS5Padding(16));
        ct1 = cipher.update(pt1);
        ct2 = cipher.doFinal(pt2);


        // 암호화 클래스
        Security.addProvider(new BouncyCastleProvider());

        // 버퍼 정의
        int BUF_SIZE = 1024;
        byte[] buffer = new byte[BUF_SIZE];
        int read = BUF_SIZE;

        // 파일스트림 생성
        FileInputStream fis = new FileInputStream(fileName);
        FileOutputStream fos = new FileOutputStream(en_fileName);

        // 초기 벡터 정의
        byte[] ivBytes = new byte[] {
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};

        // main에서 입력받은 password로 key 생성, ivBytes로 iv 생성
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // key 및 iv 헤더에 저장
        fos.write(keyBytes);
        fos.write(ivBytes);

        // AES/CBC/PKCS7Padding 인스턴스 정의
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        // 암호화 모드 초기화
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        // 진행현황 표시를 위해 변수 생성 및 fileSize 저장
        int cur = 0, percent = 1;
        int dash = 0;
        long fileSize = Files.size(Paths.get(fileName)) / 1024;

        // 파일 암호화
        System.out.println("File Encoding Start!");
        while((read = fis.read(buffer, 0, BUF_SIZE)) == BUF_SIZE){
            fos.write(cipher.update(buffer, 0, read));
            if((fileSize / 20) * percent < cur) {
                dash = 0;
                while(dash < percent) {
                    System.out.print("-");
                    dash++;
                }
                System.out.print(percent * 5 + "%\r");
                percent++;
            }
            cur++;
        }
        // 파일 마지막 부분 처리
        fos.write(cipher.doFinal(buffer, 0, read));
        System.out.println("File Encoding Done!");
        System.out.println("-----------------------------------------------");

        // 파일 스트림 종료
        fis.close();
        fos.close();
    }
    */

    /*
    public static void decode(String fileName, String en_fileName, byte[] de_keyBytes) throws Exception{
        LEA
        // 객체 생성
        BlockCipherMode cipher = new LEA.CBC();


        // 복호화
        cipher.init(Mode.DECRYPT, key, iv);
        cipher.setPadding(new PKCS5Padding(16));
        pt1 = cipher.update(ct1);
        pt2 = cipher.doFinal(ct2);


        // 복호화 클래스
        Security.addProvider(new BouncyCastleProvider());

        // 버퍼 정의
        int BUF_SIZE = 1024;
        byte[] buffer = new byte[BUF_SIZE];
        int read = BUF_SIZE;

        // 파일 스트림 생성
        FileInputStream fis = new FileInputStream(en_fileName);
        FileOutputStream fos = new FileOutputStream(fileName);

        // 헤더의 속성 길이 변수
        int headerElementLenth = 16;

        // 헤더에 저장된 keyBytes 일치 여부 확인 및 불일치 시 종료
        fis.read(buffer, 0, headerElementLenth);
        byte[] keyBytes = new byte[headerElementLenth];
        System.arraycopy(buffer, 0, keyBytes, 0, headerElementLenth);
        System.arraycopy(de_keyBytes, 0, de_keyBytes, 0, headerElementLenth);
        if(!Utils.toHexString(keyBytes).equals(Utils.toHexString(de_keyBytes))) {
            System.out.println("password authorize failed.");
            System.out.println("please try again...");
            fis.close();
            fos.close();
            return;
        }

        // key값 일치 시 복호화 진행
        System.out.println("password authorize success.");

        // 헤더에 저장된 ivBytes 저장
        fis.read(buffer, 0, headerElementLenth);
        byte[] ivBytes = new byte[headerElementLenth];
        System.arraycopy(buffer, 0, ivBytes, 0, headerElementLenth);

        // key 생성, iv 생성
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // AES/CBC/PKCS7Padding 인스턴스 정의
        Cipher cipher =  Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

        // 복호화 모드 초기화
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        // 진행현황 표시를 위해 변수 생성 및 fileSize 저장
        int cur = 0, percent = 1;
        int dash = 0;
        long fileSize = (Files.size(Paths.get(en_fileName)) - 32) / 1024 ;

        // 파일 복호화
        System.out.println("File Decoding Start!");
        while((read = fis.read(buffer, 0, BUF_SIZE)) == BUF_SIZE){
            fos.write(cipher.update(buffer, 0, read));
            if((fileSize / 20) * percent < cur) {
                dash = 0;
                while(dash < percent) {
                    System.out.print("-");
                    dash++;
                }
                System.out.print(percent * 5 + "%\r");
                percent++;
            }
            cur++;
        }

        // 파일 마지막 부분 처리
        fos.write(cipher.doFinal(buffer, 0, read));
        System.out.println("File Decoding Done!");

        // 파일 스트림 종료
        fis.close();
        fos.close();
    }
    */

    /*
    public static void main(String[] args) throws Exception{
        Scanner input = new Scanner(System.in);

        // encode password 입력 및 암호화
        System.out.print("Enter Encode Password : ");
        String enP = input.next();
        encode("SecurityProgramming_PDF.zip", "encrypted.enc", PBKDF(enP));

        // decode password 입력 및 복호화
        System.out.print("Enter Decode Password : ");
        String deP = input.next();
        decode("decrypted.zip", "encrypted.enc", PBKDF(deP));

        input.close();
    }
     */
}

