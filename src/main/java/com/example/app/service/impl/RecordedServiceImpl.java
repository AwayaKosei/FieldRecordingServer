package com.example.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.domain.Recorded;
import com.example.app.mapper.RecordedMapper;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordedServiceImpl implements RecordedService {

    private final RecordedMapper recordedMapper;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Override
    public void saveRecord(Recorded recorded) throws IOException {
        MultipartFile file = recorded.getFile();
        String originalFilename = file.getOriginalFilename();
        String ext = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            ext = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + ext;

        File dir = new File(uploadDirectory);
        if (!dir.exists()) dir.mkdirs();

        Path filePath = Paths.get(uploadDirectory, uniqueFilename).normalize();
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        recorded.setTitle(uniqueFilename);
        if (recorded.getRecordAt() == null) {
            recorded.setRecordAt(LocalDateTime.now());
        }

        recordedMapper.insert(recorded);
    }
    @Override
    public Path resolveFilePath(Recorded rec) {
        return Paths.get(uploadDirectory).resolve(rec.getTitle()).normalize();
    }


    @Override
    public List<Recorded> findAll() {
        return recordedMapper.findAll();
    }
    
    @Override
    public List<Recorded> findByUserId(Integer userId) {
        return recordedMapper.findByUserId(userId);
    }
    
    @Override
    public Recorded findByRecordId(Integer recordId) {
        return recordedMapper.findByRecordId(recordId);
    }

    @Override
    public List<Recorded> findByUserIdAndLocation(
        Integer userId,
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude
    ) {
        return recordedMapper.findByUserIdAndLocation(userId, minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    @Override
    public void delete(Integer recordId) {
        recordedMapper.deleteById(recordId);
    }
//    
//    @Value("${upload.directory}")
//    private String uploadDir;  // ex) /var/app/uploads
//
//    // 既存の saveRecord(...) では、ファイルを title で保存している前提
//    // title はユニーク名（拡張子含む）
//
//    public Path resolveFilePath(Recorded rec) {
//        // 必要なら title のサニタイズを挟む（../を弾く等）
//        return Paths.get(uploadDir).resolve(rec.getTitle()).normalize();
//    }
//
//    // 参考：初期化でディレクトリ作成
//    @PostConstruct
//    public void ensureDir() throws IOException {
//        Files.createDirectories(Paths.get(uploadDir));
//    }
    
}
