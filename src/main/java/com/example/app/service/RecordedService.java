package com.example.app.service;

import java.io.IOException; // IOExceptionをインポート
import java.util.List;

import com.example.app.domain.Recorded;

/**
 * 録音データ関連のビジネスロジックを定義するインターフェース。
 */
public interface RecordedService {

    /**
     * アップロードされた音声ファイルを保存し、データベースに情報を登録します。
     * @param recorded アップロードされたデータと関連情報
     * @throws IOException ファイルの保存中にエラーが発生した場合
     */
    void saveRecord(Recorded recorded) throws IOException; // ここに throws IOException を追加

    /**
     * 全ての録音データを取得します。
     * @return 録音データリスト
     */
    List<Recorded> findAll();

    /**
     * 指定されたユーザーIDに紐づく全ての録音データを取得します。
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
     * 指定された緯度・経度範囲で録音データを取得します。
     * @param userId ユーザーID
     * @param minLatitude 最小緯度
     * @param maxLatitude 最大緯度
     * @param minLongitude 最小経度
     * @param maxLongitude 最大経度
     * @return 録音データリスト
     */
    List<Recorded> findByUserIdAndLocation(
        Integer userId,
        double minLatitude,
        double maxLatitude,
        double minLongitude,
        double maxLongitude
    );

    /**
     * 録音データを登録します。（ファイルアップロードとは別のメタデータ登録用）
     * @param recorded 登録する録音データ
     */
    void register(Recorded recorded);

    /**
     * 指定された録音IDのデータを削除します。
     * @param recordId 削除する録音ID
     */
    void delete(Integer recordId);
}
