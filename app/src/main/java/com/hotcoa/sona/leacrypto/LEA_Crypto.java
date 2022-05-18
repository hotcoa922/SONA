package com.hotcoa.sona.leacrypto;

import static java.lang.System.arraycopy;

import kr.re.nsr.crypto.padding.PKCS5Padding;
import kr.re.nsr.crypto.symm.LEA;
import kr.re.nsr.crypto.util.Hex;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Security;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Scanner;


import android.content.Context;
import android.provider.Settings;
import android.util.Log;

public class LEA_Crypto {
    public static String toString(byte[] input) {
        return new String(input);
    }
    public static byte[] toByteArray(String string) {
        // String to ByteArray
        byte[] bytes = new byte[string.length()];
        char[] chars = string.toCharArray();

        for (int i = 0; i != chars.length; i++)
        {
            bytes[i] = (byte)chars[i];
        }

        return bytes;
    }
    public static String toHexString(byte[] input) {
        String hexString = "";
        int length = input.length;

        for(int i = 0; i < length; i++) {
            if(i != length - 1) {
                hexString += String.format("%02X:", input[i]);
            } else {
                hexString += String.format("%02X", input[i]);
            }
        }

        return hexString;
    }
    public static byte[] PBKDF(String P) throws Exception {
        // PBKDF 클래스
        byte[] S = new byte[] {0x78, 0x57, (byte)0x8e, 0x5a, 0x5d, 0x63, (byte)0xcb, 0x06};
        int c = 1000;
        int dkLen = 16;

        Security.addProvider(new BouncyCastleProvider());

        MessageDigest md = MessageDigest.getInstance("SHA1");

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
    public static String encode(String plain, byte[] keyBytes) throws Exception {
        /*
        // LEA 객체 생성
        BlockCipherMode cipher = new LEA.CBC();
        // 암호화
        cipher.init(Mode.ENCRYPT, key, iv);
        cipher.setPadding(new PKCS5Padding(16));
        ct1 = cipher.update(pt1);
        ct2 = cipher.doFinal(pt2);
        */

        // 암호화 클래스
        Security.addProvider(new BouncyCastleProvider());

        // 초기 벡터 정의
        byte[] ivBytes = new byte[] {
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};

        // main에서 입력받은 password로 key 생성, ivBytes로 iv 생성
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // AES/CBC/PKCS7Padding 인스턴스 정의
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 암호화 모드 초기화
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        // 일기 내용 암호화
        byte[] encrypted = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        return Hex.toHexString(encrypted);
    }

    public static String decode(String encrypted, byte[] keyBytes) throws Exception{
        /*
        LEA
        // 객체 생성
        BlockCipherMode cipher = new LEA.CBC();


        // 복호화
        cipher.init(Mode.DECRYPT, key, iv);
        cipher.setPadding(new PKCS5Padding(16));
        pt1 = cipher.update(ct1);
        pt2 = cipher.doFinal(ct2);
        */

        // 복호화 클래스
        Security.addProvider(new BouncyCastleProvider());

        // 초기 벡터 정의
        byte[] ivBytes = new byte[] {
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
                0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};

        // key 생성, iv 생성
        SecretKeySpec key = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivBytes);

        // AES/CBC/PKCS7Padding 인스턴스 정의
        Cipher cipher =  Cipher.getInstance("AES/CBC/PKCS5Padding");

        // 복호화 모드 초기화
        cipher.init(Cipher.DECRYPT_MODE, key, iv);

        // 일기 내용 복호화
        byte[] encBytes = Hex.decodeHexString(encrypted);
        return new String(cipher.doFinal(encBytes), "UTF-8");
    }

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

