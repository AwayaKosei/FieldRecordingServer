package com.example.app.controller;

import java.util.Collections;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.User;
import com.example.app.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(
	  origins = "http://localhost:5173"
	  //, allowCredentials = true,
	  // methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.OPTIONS }
	)
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * ログイン処理を行います。
     * 認証に成功した場合、ユーザー情報とHTTPステータス200を返します。
     * 失敗した場合、エラーメッセージとHTTPステータス401を返します。
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload, HttpSession session) {
        String email = payload.get("email");
        String password = payload.get("password");

        User user = userService.getAuthenticatedUser(email, password);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                 .body(Collections.singletonMap("message", "Invalid email or password"));
        }

        // セッションにユーザー情報を保存
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getUserId());
        return ResponseEntity.ok(user);
    }
    
    /**
     * ログイン中のユーザー情報を取得します。
     * 【テスト用】セッションのチェックをスキップし、常にダミーユーザーを返します。
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        // --- 本番環境に戻す際は、以下のコードをコメントアウトまたは削除し、元のロジックに戻してください ---
//        User dummyUser = new User();
//        dummyUser.setUserId(9999); // テスト用のID
//        dummyUser.setUserName("Test User");
//        dummyUser.setEmail("test@example.com");
//        return ResponseEntity.ok(dummyUser);
        // --------------------------------------------------------------------------------------

         //元のロジック
         User user = (User) session.getAttribute("user");
         if (user == null) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                  .body(Collections.singletonMap("message", "Not logged in"));
         }
         return ResponseEntity.ok(user);
    }

    /**
     * 新しいユーザーを登録します。
     * 登録後、自動的にセッションを確立してユーザー情報を返します。
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user, HttpSession session) {
        try {
            userService.register(user);
            // 登録成功後、自動的にログイン状態にする
            session.setAttribute("user", user);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                 .body(Collections.singletonMap("message", "Registration failed."));
        }
    }

    /**
     * ログアウト処理を行います。
     * セッションを無効化します。
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // セッションを無効化
        return ResponseEntity.ok(Collections.singletonMap("message", "Logged out successfully"));
    }
}
