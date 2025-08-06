package com.example.app.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.Recorded;

@Mapper
public interface RecordedMapper {

    /**
     * 全ての録音データを取得します。
     * @return 録音データリスト
     */
    List<Recorded> findAll();

    /**
     * 指定されたユーザーIDに紐づく録音データを全て取得します。
     * @param userId ユーザーID
     * @return 録音データリスト
     */
    List<Recorded> findByUserId(Integer userId);
    
    /**
     * 指定された録音IDでデータを取得します。
     * @param recordId 録音ID
     * @return 録音データ
     */
    Recorded findByRecordId(Integer recordId); 

    /**
     * 特定のユーザーIDと緯度・経度の範囲でデータを取得します。
     * @param userId ユーザーID
     * @param minLatitude 最小緯度
     * @param maxLatitude 最大緯度
     * @param minLongitude 最小経度
     * @param maxLongitude 最大経度
     * @return 録音データリスト
     */
    List<Recorded> findByUserIdAndLocation(
        @Param("userId") Integer userId,
        @Param("minLatitude") double minLatitude,
        @Param("maxLatitude") double maxLatitude,
        @Param("minLongitude") double minLongitude,
        @Param("maxLongitude") double maxLongitude
    );
    
    /**
     * 新しい録音データを登録します。
     * @param recorded 登録する録音データ
     * @return 挿入された件数
     */
    int insert(Recorded recorded);

    // /**
    //  * 録音データを更新します。
    //  * @param recorded 更新する録音データ
    //  * @return 更新された件数
    //  */
    // int update(Recorded recorded); // 不要のため削除

    /**
     * 指定された録音IDでデータを削除します。
     * @param recordId 削除する録音ID
     * @return 削除された件数
     */
    int deleteById(Integer recordId);
}
