package com.game.dao;

import com.game.dto.SignUp;

public interface UserDao {
public void signup(SignUp signUp);
public String getAccessToken(String userName,String password);
}
