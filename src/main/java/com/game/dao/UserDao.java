package com.game.dao;

import com.game.dto.SignUp;

public interface UserDao {
public void signup(SignUp signUp);
public byte[] getAccessToken(String userName,String password);
}
