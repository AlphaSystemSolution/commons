package com.alphasystem.util;

/**
 * 
 */
import static com.alphasystem.util.Base64.decode;
import static com.alphasystem.util.Base64.encode;
import static java.lang.System.out;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;
import static javax.crypto.Cipher.getInstance;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author sali
 * 
 */
public class PasswordHasher {

	private static final String ALGORITHM = "DES/CTR/NoPadding";

	private static Cipher cipher;

	private static final byte[] IV_BYTES = new byte[] { 0x07, 0x06, 0x05, 0x04,
			0x03, 0x02, 0x01, 0x00 };

	private static IvParameterSpec ivSpec;

	private static SecretKeySpec key;

	private static final byte[] KEY_BYTES = new byte[] { 0x01, 0x23, 0x45,
			0x67, (byte) 0x89, (byte) 0xab, (byte) 0xcd, (byte) 0xef };

	private static final String PROVIDER = "BC";

	static {
		Security.addProvider(new BouncyCastleProvider());
		key = new SecretKeySpec(KEY_BYTES, "DES");
		ivSpec = new IvParameterSpec(IV_BYTES);
		try {
			cipher = getInstance(ALGORITHM, PROVIDER);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String decrypt(String inputString, int length) {
		byte[] cipherText = decode(inputString);
		try {
			cipher.init(DECRYPT_MODE, key, ivSpec);
			byte[] plainText = new byte[cipher.getOutputSize(length)];
			int ptLength = cipher.update(cipherText, 0, length, plainText, 0);
			ptLength += cipher.doFinal(plainText, ptLength);
			String s = new String(plainText);
			if (plainText.length > ptLength) {
				s = s.substring(0, s.length() - 1);
			}
			return s;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String encrypt(String inputString) {
		byte[] input = inputString.getBytes();
		try {
			cipher.init(ENCRYPT_MODE, key, ivSpec);
			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
			ctLength += cipher.doFinal(cipherText, ctLength);
			return new String(encode(cipherText));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void main(String[] args) {
		String encrypt = encrypt("something");
		out.println(encrypt);
		String decrypt = decrypt("ZvCkynZ3WWzs", 9);
		out.println(decrypt + " : " + decrypt.length());
	}

}
