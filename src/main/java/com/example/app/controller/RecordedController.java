package com.example.app.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Recorded;
import com.example.app.domain.User;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173"
// , methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}, allowCredentials = "true", maxAge = 3600
)
public class RecordedController {

    private final RecordedService recordedService;

    // ヘルパーメソッド：セッションからユーザーIDを取得 (テスト用)
    private Integer getUserIdFromSession(HttpSession session) {
      Object id = session.getAttribute("userId");
      if (id instanceof Integer i) {
          return i;
      }
      if (id instanceof String s) {
          try {
              return Integer.valueOf(s);
          } catch (NumberFormatException e) {
              // ログを出すか無視
          }
      }
      Object u = session.getAttribute("user");
      if (u instanceof User user) {
          return user.getUserId();
      }
      return null;
  }


    /**
     * 音声ファイルをアップロードするエンドポイント。
     * @param recorded アップロードするファイル情報を含むRecordedオブジェクト
     * @param session HTTPセッション
     * @return 成功メッセージ
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@ModelAttribute Recorded recorded, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not logged in");
        }

        recorded.setUserId(userId);
        
        if (recorded.getFile() == null || recorded.getFile().isEmpty()) {
            return ResponseEntity.badRequest().body("アップロードするファイルがありません。");
        }

        try {
            recordedService.saveRecord(recorded);
            return ResponseEntity.ok("ファイルが正常にアップロードされ、情報が登録されました。");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ファイルのアップロード中にエラーが発生しました。");
        }
    }

    /**
     * 全ての録音データを取得します。
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
    
    // --- 既存のAPIエンドポイント ---
    @GetMapping("/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable Integer recordId, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }
        Recorded record = recordedService.findByRecordId(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        return ResponseEntity.ok(record);
    }
    
    
    
    

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
    
    @GetMapping("/{recordId}/file")
    public ResponseEntity<org.springframework.core.io.Resource> getFile(
            @PathVariable Integer recordId,
            jakarta.servlet.http.HttpSession session) throws java.io.IOException {

        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Recorded rec = recordedService.findByRecordId(recordId);
        if (rec == null || !rec.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        java.nio.file.Path path = recordedService.resolveFilePath(rec);
        if (!java.nio.file.Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        String mime = java.nio.file.Files.probeContentType(path);
        org.springframework.core.io.UrlResource resource = new org.springframework.core.io.UrlResource(path.toUri());

        return ResponseEntity.ok()
                .header("Content-Type", mime != null ? mime : "audio/webm")
                .header("Cache-Control", "no-store")
                // ブラウザ内再生を想定：inline。ダウンロードさせたいなら attachment に変更
                // .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + rec.getTitle() + "\"")
                .body(resource);
    }

    
    

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
        Integer userId = getUserIdFromSession(session);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Not logged in"));
        }
        Recorded record = recordedService.findByRecordId(recordId);
        if (record == null || !record.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        recordedService.delete(recordId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Record deleted successfully"));
    }
}
