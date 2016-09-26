package com.game.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import com.game.dao.UserDaoImp;
import com.game.dto.SignUp;

public class Oauth {
	public static String ALGORITHM = "RSA";
	KeyPair keyPair;
	ObjectInputStream inputStream;
	private static String API_KEY = "/home/bridgelabz6/Music/API_KEY.key";
	//private static String SECRET_KEY = "/home/bridgelabz6/Music/SECRET_KEY.key";

	public String getAccesToken(SignUp signUp) throws FileNotFoundException, IOException, ClassNotFoundException {

		// SECRET_KEY=signUp.getSecretKey();
		
		String originalText = signUp.getUserName() + " " + signUp.getPassword();

		// Encrypt the string using the public key

		inputStream = new ObjectInputStream(new FileInputStream(API_KEY));
		PublicKey publicKey = (PublicKey) inputStream.readObject();

		String accessToken = null;
		try {

			// PublicKey publicKey = keyPair.getPublic();
			System.out.println("public key:" + publicKey.getFormat());

			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			accessToken = cipher.doFinal(originalText.getBytes()).toString();

			System.out.println("access token:" + accessToken.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return accessToken;
	}

	/*public String decrypt(byte[] accessToken) {
		byte[] dectyptedText = null;

		try {
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			inputStream = new ObjectInputStream(new FileInputStream(SECRET_KEY));
			PrivateKey key = (PrivateKey) inputStream.readObject();

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(accessToken);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}*/

	public boolean authorisedUser(SignUp signUp) throws FileNotFoundException, ClassNotFoundException, IOException {
		Oauth oauth = new Oauth();
		UserDaoImp userDaoImp = new UserDaoImp();

		String accessToken = oauth.getAccesToken(signUp);
		byte[] dbAccessToken = userDaoImp.getAccessToken(signUp.getUserName(), signUp.getPassword());

		if (accessToken.equals(dbAccessToken))
			return true;
		else
			return false;
	}

}
