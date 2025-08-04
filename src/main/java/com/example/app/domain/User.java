package com.example.app.domain;

import lombok.Data;

@Data
public class User {
//	ユーザーID
	private Integer userId;
//	ユーザー名
	private String userName;
// Email
	private String email;
//	パスワード
	private String password;
//  音源保持上限
	private Integer capacity;
	
}
