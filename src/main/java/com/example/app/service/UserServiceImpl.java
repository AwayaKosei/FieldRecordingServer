package com.example.app.service;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.User;
import com.example.app.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    // finalキーワードで宣言することで、コンストラクタインジェクションを強制
    private final UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User findById(Integer userId) {
        return userMapper.findById(userId);
    }
    
    @Override
    public User getAuthenticatedUser(String email, String password) {
        User user = userMapper.findByEmail(email);
		
		// ユーザーが存在しない場合
		if(user == null) {
			System.out.println("ログイン失敗: メールアドレス間違い");
			return null;
		}
		
		// BCryptでハッシュ化されたパスワードと入力されたパスワードを比較
		if(!BCrypt.checkpw(password, user.getPassword())) {
			System.out.println("ログイン失敗: パスワード間違い");
			return null;
		}
		
		return user;
    }

    @Override
    public void register(User user) {
        // パスワードをBCryptでハッシュ化してからデータベースに保存する
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userMapper.insert(user);
    }
    
    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void delete(Integer userId) {
        userMapper.deleteById(userId);
    }
}
