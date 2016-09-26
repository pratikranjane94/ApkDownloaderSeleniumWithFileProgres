package com.game.model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.game.dto.SignUp;

public class OauthGenerator {

	private final static String apiKey = "abcdefgh";
	SecretKeySpec secretKeySpec;
	// Get the secret Key using user name and password
	public byte[] oAuthKeyCreator(SignUp signUp) throws UnsupportedEncodingException, NoSuchAlgorithmException {
		// key use for encryption
		String key = "DirectApkDownloader";

		byte[] secretKey = (key + signUp.getUserName() + signUp.getPassword()).getBytes("UTF-8");

		MessageDigest sha = MessageDigest.getInstance("SHA-1");
		secretKey = sha.digest(secretKey);
		secretKey = Arrays.copyOf(secretKey, 16); // use only first 128 bit

		return secretKey;
	}

	public byte[] accessToken(SignUp signUp) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		secretKeySpec = new SecretKeySpec(signUp.getSecretKey(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

		byte[] accessToken = cipher.doFinal((apiKey + " " + signUp.getUserName() + " "+signUp.getPassword()).getBytes());
		return accessToken;
	}

	public ArrayList<String> authenticateUser(byte[] accessToken) throws NoSuchAlgorithmException,
			NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		ArrayList<String> info=new ArrayList<>();
		String username=null;
		// getting original data from access token
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
		byte[] original = cipher.doFinal(accessToken);

		String originalString = new String(original);
		System.out.println("Original string: " + originalString);
		String[] a = originalString.split(" ");
		for (String string : a) {
			info.add(string);
		}
		for (String string : a) {
			System.out.println("deatils:"+string);
		}
		return info;
	}
}
