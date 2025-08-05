package com.example.app.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Recorded;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class RecordedController {

    private final RecordedService recordedService;

    // ヘルパーメソッド：セッションからユーザーIDを取得
    private Integer getUserIdFromSession(HttpSession session) {
        // --- 本番環境に戻す際は、以下の行をコメントアウトまたは削除し、元のロジックに戻してください ---
        // テスト用に常にユーザーID=1を返す
        return Integer.valueOf(1);
        // --------------------------------------------------------------------------------------

        // 元のロジック
        // User user = (User) session.getAttribute("user");
        // return (user != null) ? user.getUserId() : null;
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

        Recorded record = recordedService.findById(recordId);
        
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
    @GetMapping("/search")
    public ResponseEntity<?> searchByLocation(@RequestParam Map<String, String> params, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }

        try {
            double minLat = Double.parseDouble(params.get("minLat"));
            double maxLat = Double.parseDouble(params.get("maxLat"));
            double minLon = Double.parseDouble(params.get("minLon"));
            double maxLon = Double.parseDouble(params.get("maxLon"));

            List<Recorded> records = recordedService.findByUserIdAndLocation(userId, minLat, maxLat, minLon, maxLon);
            return ResponseEntity.ok(records);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("message", "Invalid coordinate format"));
        }
    }
    
    /**
     * 新しい録音データを登録します。
     * @param recorded 登録する録音データ
     * @param session HTTPセッション
     * @return 登録された録音データ
     */
    @PostMapping
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

        Recorded record = recordedService.findById(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        
        recordedService.delete(recordId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Record deleted successfully"));
    }
}
