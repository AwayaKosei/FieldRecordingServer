package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.User;

@Mapper
public interface UserMapper {

    /**
     * 全てのユーザーを取得します。
     * @return ユーザーリスト
     */
    List<User> findAll();

    /**
     * 指定されたユーザーIDでユーザーを取得します。
     * @param userId ユーザーID
     * @return ユーザー
     */
    User findById(Integer userId);

    /**
     * 指定されたメールアドレスでユーザーを取得します。
     * @param email メールアドレス
     * @return ユーザー
     */
    User findByEmail(String email);

    /**
     * 指定されたメールアドレスとパスワードでユーザーを取得します。
     * @param email メールアドレス
     * @param password パスワード
     * @return ユーザー
     */
    User findByEmailAndPassword(@Param("email") String email, @Param("password") String password);

    /**
     * 新しいユーザーを登録します。
     * @param user 登録するユーザー情報
     * @return 挿入された件数
     */
    int insert(User user);

    /**
     * ユーザー情報を更新します。
     * @param user 更新するユーザー情報
     * @return 更新された件数
     */
    int update(User user);

    /**
     * 指定されたユーザーIDでユーザーを削除します。
     * @param userId 削除するユーザーID
     * @return 削除された件数
     */
    int deleteById(Integer userId);
}
