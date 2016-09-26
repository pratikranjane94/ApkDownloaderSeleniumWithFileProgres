package com.game.dto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class SignUp {
	@Id
	@GeneratedValue
	private int id;
	@Column
	private String name;
	@Column
	private String UserName;
	@Column
	private String emailId;
	@Column
	private String Password;
	@Column
	private byte[] secretKey;
	@Column
	private byte[] accessToken;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public byte[] getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(byte[] secretKey) {
		this.secretKey = secretKey;
	}

	public byte[] getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(byte[] accessToken) {
		this.accessToken = accessToken;
	}

	

	


}
