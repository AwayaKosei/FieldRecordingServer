package com.example.app.dto;

import com.example.app.domain.User;

public record UserDto(Integer userId, String userName, String email) {
	public static UserDto from(User u) {
    if (u == null) return null;
    return new UserDto(u.getUserId(), u.getUserName(), u.getEmail());
	}
}


