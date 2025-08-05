package com.example.app.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.app.domain.Recorded;
import com.example.app.mapper.RecordedMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RecordedServiceImpl implements RecordedService {

    // finalキーワードで宣言することで、コンストラクタインジェクションを強制
    private final RecordedMapper recordedMapper;

    @Override
    public List<Recorded> findAll() {
        return recordedMapper.findAll();
    }
    
    @Override
    public List<Recorded> findByUserId(Integer userId) {
        return recordedMapper.findByUserId(userId);
    }
    
    @Override
    public Recorded findById(Integer recordId) {
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
    public void register(Recorded recorded) {
        recordedMapper.insert(recorded);
    }

    @Override
    public void update(Recorded recorded) {
        recordedMapper.update(recorded);
    }
    
    @Override
    public void delete(Integer recordId) {
        recordedMapper.deleteById(recordId);
    }
}
