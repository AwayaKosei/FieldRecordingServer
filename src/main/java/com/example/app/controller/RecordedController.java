package com.example.app.controller;

import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute; // @ModelAttribute をインポート
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile; // MultipartFile をインポート

import com.example.app.domain.Recorded;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@CrossOrigin(origins = "http://localhost:5173", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true", maxAge = 3600)
@RequiredArgsConstructor
public class RecordedController {

    private final RecordedService recordedService;

    // ヘルパーメソッド：セッションからユーザーIDを取得
    private Integer getUserIdFromSession(HttpSession session) {
        // テスト用に常にユーザーID=1を返す
        return Integer.valueOf(1);
    }

    /**
     * 音声ファイルをアップロードするエンドポイント。
     * @param recorded アップロードするファイル情報を含むRecordedオブジェクト
     * @param session HTTPセッション
     * @return 成功メッセージ
     */
    @PostMapping("/upload") // このメソッドを追加
    public ResponseEntity<String> uploadAudio(@ModelAttribute Recorded recorded, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        recorded.setUserId(userId);
        MultipartFile file = recorded.getFile();

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("アップロードするファイルがありません。");
        }

        try {
            recordedService.saveRecord(recorded);
            return ResponseEntity.ok("ファイルが正常にアップロードされ、情報が登録されました。");
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ファイルのアップロード中にエラーが発生しました。");
        }
    }

    /**
     * ログイン中のユーザーに紐づく全ての録音データを取得します。
     * @param session HTTPセッション
     * @return 録音データリスト
     */
    @GetMapping
    public ResponseEntity<?> getAllRecords(HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }

        List<Recorded> records = recordedService.findByUserId(userId);
        return ResponseEntity.ok(records);
    }
    
    /**
     * 特定のIDの録音データを取得します。
     * @param recordId 録音ID
     * @param session HTTPセッション
     * @return 録音データ
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable Integer recordId, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }

        Recorded record = recordedService.findByRecordId(recordId); // findById から findByRecordId に修正
        
        // 他のユーザーのデータにアクセスしようとしていないかチェック
        if (record == null || !record.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        
        return ResponseEntity.ok(record);
    }

    /**
     * 緯度・経度範囲で録音データを検索します。
     * @param params 検索パラメータ
     * @param session HTTPセッション
     * @return 検索結果の録音データリスト
     */
    // @GetMapping("/search") はコメントアウトされたままにしておきます
    // public ResponseEntity<?> searchByLocation(@RequestParam Map<String, String> params, HttpSession session) { ... }
    
    /**
     * 新しい録音データを登録します。
     * @param recorded 登録する録音データ
     * @param session HTTPセッション
     * @return 登録された録音データ
     */
    @PostMapping("/create") // 競合を避けるためにパスを明示
    public ResponseEntity<?> createRecord(@RequestBody Recorded recorded, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }

        // 登録するデータにログイン中のユーザーIDを設定
        recorded.setUserId(userId);
        recordedService.register(recorded);

        return ResponseEntity.status(HttpStatus.CREATED).body(recorded);
    }
    
    /**
     * 特定のIDの録音データを削除します。
     * @param recordId 削除する録音ID
     * @param session HTTPセッション
     * @return 成功メッセージ
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }

        Recorded record = recordedService.findByRecordId(recordId); // findById から findByRecordId に修正
        if (record == null || !record.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        
        recordedService.delete(recordId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Record deleted successfully"));
    }
}
