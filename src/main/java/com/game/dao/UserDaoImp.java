package com.game.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import com.game.dto.SignUp;

@Repository("userDaoImp")
public class UserDaoImp implements UserDao {

	@Resource(name = "sessionFactory")
	SessionFactory sessionFactory;

	Session session;

	@Override
	public void signup(SignUp signUp) {

		session = sessionFactory.openSession();
		Transaction tr = session.beginTransaction();
		session.save(signUp);
		tr.commit();
		System.out.println("User signed up");
		session.close();
	}

	@SuppressWarnings("unchecked")
	public byte[] getAccessToken(String userName, String password) {
		byte[] accessToken=null;
		session = sessionFactory.openSession();
		System.out.println("username and password:"+userName+" "+password);
		Query query = session.createQuery("from SignUp where userName=:userName and password=:password");
		query.setString("userName", userName);
		query.setString("password", password);
		List<SignUp> list = query.list();
		for (SignUp signUp : list) {
			accessToken = signUp.getAccessToken();
		}
		System.out.println("database call:"+accessToken);
		session.close();
		return accessToken;
		
	}

}
