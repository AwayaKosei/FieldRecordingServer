package com.example.app.service;

import java.util.List;

import com.example.app.domain.User;

public interface UserService {

    /**
     * 全てのユーザーを取得します。
     * @return ユーザーリスト
     */
    List<User> findAll();

    /**
     * 指定されたIDでユーザーを取得します。
     * @param userId ユーザーID
     * @return ユーザー
     */
    User findById(Integer userId);

    /**
     * ログイン認証を行います。
     * @param email メールアドレス
     * @param password パスワード
     * @return 認証に成功した場合はUserオブジェクト、失敗した場合はnull
     */
    User getAuthenticatedUser(String email, String password);

    /**
     * 指定されたメールアドレスでユーザーを取得します。
     * @param email メールアドレス
     * @return ユーザー
     */
    User findByEmail(String email);

    /**
     * 新しいユーザーを登録します。
     * @param user 登録するユーザー情報
     */
    void register(User user);

    /**
     * ユーザー情報を更新します。
     * @param user 更新するユーザー情報
     */
    void update(User user);
    
    /**
     * 指定されたIDのユーザーを削除します。
     * @param userId 削除するユーザーID
     */
    void delete(Integer userId);
}

