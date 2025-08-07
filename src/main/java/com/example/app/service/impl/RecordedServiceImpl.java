package com.example.app.service.impl;

import java.io.File; // Fileをインポート
import java.io.IOException; // IOExceptionをインポート
import java.nio.file.Files; // Filesをインポート
import java.nio.file.Path; // Pathをインポート
import java.nio.file.Paths; // Pathsをインポート
import java.time.LocalDateTime; // LocalDateTimeをインポート
import java.util.List;
import java.util.UUID; // UUIDをインポート

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Transactionalをインポート
import org.springframework.web.multipart.MultipartFile; // MultipartFileをインポート

import com.example.app.domain.Recorded;
import com.example.app.mapper.RecordedMapper;
import com.example.app.service.RecordedService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional // トランザクション管理を有効にする
public class RecordedServiceImpl implements RecordedService {

    private final RecordedMapper recordedMapper;

    @Value("${upload.directory}")
    private String uploadDirectory;

    @Override
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
        
        // recordAt が null の場合に現在日時を設定
        if (recorded.getRecordAt() == null) {
            recorded.setRecordAt(LocalDateTime.now());
        }

        recordedMapper.insert(recorded);
    }

    @Override
    public List<Recorded> findAll() {
        return recordedMapper.findAll();
    }
    
    @Override
    public List<Recorded> findByUserId(Integer userId) {
        List<Recorded> records = recordedMapper.findByUserId(userId);

        return records;
    }
    
    @Override
    public Recorded findByRecordId(Integer recordId) {
        Recorded record = recordedMapper.findByRecordId(recordId);

        return record;
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
    public void register(Recorded recorded) {
        recordedMapper.insert(recorded);
    }

//    @Override
//    public void update(Recorded recorded) {
//        recordedMapper.update(recorded);
//    }
    
    @Override
    public void delete(Integer recordId) {
        recordedMapper.deleteById(recordId);
    }
}
