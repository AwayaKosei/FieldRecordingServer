package com.example.app.service.impl; // パッケージ名をimplに変更

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.app.domain.Recorded;
import com.example.app.mapper.RecordedMapper;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecordedServiceImpl implements RecordedService { // インターフェースを実装

    private final RecordedMapper recordedMapper;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Override // インターフェースのメソッドを実装
    public void saveRecord(Recorded recorded) throws IOException {
        MultipartFile file = recorded.getFile();

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        Path filePath = Paths.get(uploadDirectory, uniqueFilename);

        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Files.copy(file.getInputStream(), filePath);

        recorded.setTitle(uniqueFilename);
        
        if (recorded.getRecordAt() == null) {
            recorded.setRecordAt(LocalDateTime.now());
        }

        recordedMapper.insert(recorded);
    }

    @Override // インターフェースのメソッドを実装
    public List<Recorded> findAll() {
        return recordedMapper.findAll();
    }
    
    @Override // インターフェースのメソッドを実装
    public List<Recorded> findByUserId(Integer userId) {
        return recordedMapper.findByUserId(userId);
    }
    
    @Override // インターフェースのメソッドを実装
    public Recorded findByRecordId(Integer recordId) {
        return recordedMapper.findByRecordId(recordId); // ここを修正 (findById -> findByRecordId)
    }

    @Override // インターフェースのメソッドを実装
    public List<Recorded> findByUserIdAndLocation(
        Integer userId,
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude
    ) {
        return recordedMapper.findByUserIdAndLocation(userId, minLatitude, maxLatitude, minLongitude, maxLongitude);
    }
    
    @Override // インターフェースのメソッドを実装
    public void register(Recorded recorded) {
        // このregisterはDBへのメタデータ登録を想定しており、ファイルアップロードはsaveRecordで行う
        recordedMapper.insert(recorded);
    }

    @Override // インターフェースのメソッドを実装
    public void delete(Integer recordId) {
        recordedMapper.deleteById(recordId);
    }
}
