package com.example.app.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
//  音源保持上限
	private Integer capacity;
	
}
