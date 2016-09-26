package com.game.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.game.dao.UserDao;
import com.game.dao.UserDaoImp;
import com.game.dto.SignUp;
import com.game.model.Oauth;
import com.game.model.OauthGenerator;

@Controller
public class LogInController {
	OauthGenerator oauthGenerator = new OauthGenerator();

	@Resource(name = "userDaoImp")
	private UserDaoImp userDaoImp;

	@RequestMapping(value = "/openSignUp", method = RequestMethod.GET)
	public String openSignUp() {
		return "register";

	}

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public String signUp(@ModelAttribute("signup") SignUp signUp)
			throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, FileNotFoundException, ClassNotFoundException, IOException {
		// SignUp signUp=new SignUp();
		System.out.println("sign up");
		System.out.println("username:" + signUp.getUserName());

		// setting secret key
		// byte[] secretKey = (signUp.getUserName() +
		// signUp.getPassword()).getBytes("UTF-8");

		byte[] secretKey = oauthGenerator.oAuthKeyCreator(signUp);

		signUp.setSecretKey(secretKey);

		byte[] accessToken = oauthGenerator.accessToken(signUp);

		// setting access token
		signUp.setAccessToken(accessToken);

		userDaoImp.signup(signUp);
		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public @ResponseBody String login(@ModelAttribute("login") SignUp signUp)
			throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

		System.out.println("login password:" + signUp.getPassword());

		// getting access token of logged in user
		byte[] secretKey = oauthGenerator.oAuthKeyCreator(signUp);
		signUp.setSecretKey(secretKey);

		byte[] accessToken = oauthGenerator.accessToken(signUp);

		// getting access token of same user in database
		byte[] xyz = userDaoImp.getAccessToken(signUp.getUserName(), signUp.getPassword());

		System.out
				.println("login access token:" + accessToken.toString() + "/n database access token:" + xyz.toString());

		ArrayList<String> a = oauthGenerator.authenticateUser(accessToken);
		ArrayList<String> b = oauthGenerator.authenticateUser(xyz);

		System.out.println("details1:" + a + " \nDetails2:" + b);

		if (a.get(1).equals(b.get(1)) && (a.get(2).equals(b.get(2)))) {
			System.out.println("success");
			return "success";
		} else
			return "error";
	}

	/*
	  @RequestMapping(value = "/login", method = RequestMethod.POST)
	  public @ResponseBody String login(@ModelAttribute("login") SignUp signUp)
	  throws FileNotFoundException, ClassNotFoundException, IOException {
	  
	  Oauth oauth = new Oauth(); byte[] secretKey = (signUp.getUserName() +
	  signUp.getPassword()).getBytes("UTF-8"); // oauth.generateKey();
	  signUp.setSecretKey(secretKey.toString());
	  
	  System.out.println("login"); System.out.println(
	  "login username and password:" + signUp.getPassword() +
	  signUp.getPassword());
	  
	  String accessToken = oauth.getAccesToken(signUp);
	  
	  if (oauth.authorisedUser(signUp)) return "success"; else return "error";
	  }
	 */

}
