// src/main/java/com/example/app/controller/RecordedController.java
package com.example.app.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.app.domain.Recorded;
import com.example.app.domain.User;
import com.example.app.dto.RecordedDto;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
@CrossOrigin(
  origins = "http://localhost:5173",
  methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
  allowCredentials = "true",
  maxAge = 3600
)
public class RecordedController {

    private final RecordedService recordedService;

    @Value("${upload.directory}")
    private String uploadDirectory;

    private User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Collections.singletonMap("message", "Not logged in"));
    }

    private String buildFileUrl(Integer recordId) {
        // 必要に応じてホスト/ポートは動的生成に変更可
        return "http://localhost:8080/api/records/" + recordId + "/file";
    }

    private RecordedDto toDto(Recorded r) {
        return new RecordedDto(
            r.getRecordId(),
            r.getUserId(),
            r.getTitle(),
            r.getLatitude(),
            r.getLongitude(),
            r.getRecordAt(),
            buildFileUrl(r.getRecordId())
        );
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadAudio(@ModelAttribute Recorded recorded, HttpSession session) {
        User u = getCurrentUser(session);
        if (u == null) return (ResponseEntity<String>) unauthorized();

        recorded.setUserId(u.getUserId());
        if (recorded.getFile() == null || recorded.getFile().isEmpty()) {
            return ResponseEntity.badRequest().body("アップロードするファイルがありません。");
        }
        try {
            recordedService.saveRecord(recorded);
            return ResponseEntity.ok("ファイルが正常にアップロードされ、情報が登録されました。");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("ファイルのアップロード中にエラーが発生しました。");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRecords(HttpSession session) {
        User u = getCurrentUser(session);
        if (u == null) return unauthorized();

        List<Recorded> records = recordedService.findByUserId(u.getUserId());
        // 新しい順（recordAt 降順、null は最後）
        records.sort(Comparator.comparing(Recorded::getRecordAt,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        List<RecordedDto> dto = records.stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{recordId}")
    public ResponseEntity<?> getRecordById(@PathVariable Integer recordId, HttpSession session) {
        User u = getCurrentUser(session);
        if (u == null) return unauthorized();

        Recorded record = recordedService.findByRecordId(recordId);
        if (record == null || !record.getUserId().equals(u.getUserId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        return ResponseEntity.ok(toDto(record));
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByLocation(@RequestParam Map<String, String> params, HttpSession session) {
        User u = getCurrentUser(session);
        if (u == null) return unauthorized();

        try {
            double minLat = Double.parseDouble(params.get("minLat"));
            double maxLat = Double.parseDouble(params.get("maxLat"));
            double minLon = Double.parseDouble(params.get("minLon"));
            double maxLon = Double.parseDouble(params.get("maxLon"));
            List<Recorded> records = recordedService.findByUserIdAndLocation(u.getUserId(), minLat, maxLat, minLon, maxLon);
            List<RecordedDto> dto = records.stream().map(this::toDto).collect(Collectors.toList());
            return ResponseEntity.ok(dto);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("message", "Invalid coordinate format"));
        }
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<?> deleteRecord(@PathVariable Integer recordId, HttpSession session) {
        User u = getCurrentUser(session);
        if (u == null) return unauthorized();

        Recorded record = recordedService.findByRecordId(recordId);
        if (record == null || !record.getUserId().equals(u.getUserId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Record not found or access denied"));
        }
        recordedService.delete(recordId);
        return ResponseEntity.ok(Collections.singletonMap("message", "Record deleted successfully"));
    }

    // 追加：音源配信（詳細での再生用）
    @GetMapping("/{recordId}/file")
    public ResponseEntity<Resource> getRecordFile(@PathVariable Integer recordId, HttpSession session) throws IOException {
        User u = getCurrentUser(session);
        if (u == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Recorded record = recordedService.findByRecordId(recordId);
        if (record == null || !record.getUserId().equals(u.getUserId())) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Path path = Paths.get(uploadDirectory, record.getTitle());
        if (!Files.exists(path)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        String contentType = Files.probeContentType(path);
        if (contentType == null) {
            String fn = record.getTitle().toLowerCase();
            if (fn.endsWith(".webm")) contentType = "audio/webm";
            else if (fn.endsWith(".wav")) contentType = "audio/wav";
            else if (fn.endsWith(".mp3")) contentType = "audio/mpeg";
            else contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        FileSystemResource resource = new FileSystemResource(path);
        String encoded = URLEncoder.encode(record.getTitle(), StandardCharsets.UTF_8).replace("+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encoded)
                .cacheControl(CacheControl.noCache())
                .body(resource);
    }
}
