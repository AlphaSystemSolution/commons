package com.alphasystem.commons.util;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Security;

import static com.alphasystem.commons.util.Base64.decode;
import static com.alphasystem.commons.util.Base64.encode;
import static javax.crypto.Cipher.*;

/**
 * @author sali
 * 
 */
public class PasswordHasher {

	private static final String ALGORITHM = "DES/CTR/NoPadding";

	private static final Cipher cipher;

	private static final byte[] IV_BYTES = new byte[] { 0x07, 0x06, 0x05, 0x04,
			0x03, 0x02, 0x01, 0x00 };

	private static final IvParameterSpec ivSpec;

	private static final SecretKeySpec key;

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

	/**
	 * Decrypt given input string.
	 *
	 * @param inputString input string to decrypt
	 * @param length length of original string
	 * @return Decrypted password
	 */
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

	/**
	 * Encrypt given string.
	 *
	 * @param inputString input string to encrypt
	 * @return Encrypted string
	 */
	public static String encrypt(String inputString) {
		byte[] input = inputString.getBytes();
		try {
			cipher.init(ENCRYPT_MODE, key, ivSpec);
			byte[] cipherText = new byte[cipher.getOutputSize(input.length)];
			int ctLength = cipher.update(input, 0, input.length, cipherText, 0);
			cipher.doFinal(cipherText, ctLength);
			return encode(cipherText);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
