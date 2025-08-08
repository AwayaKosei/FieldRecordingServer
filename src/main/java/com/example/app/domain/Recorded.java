package com.example.app.domain;

import java.time.LocalDateTime;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class Recorded {
	//ID（int 自動生成）
	private Integer recordId;
	
	//タイトル(音源ファイル名) - ここに保存されたユニークなファイル名が入ります
	private String title;
	
    //音声ファイル自体 - クライアントからのアップロード時に使用
    private MultipartFile file;

	//録音者ID
	private Integer userId;
	
	//録音日時
	private LocalDateTime recordAt;//TODO UTCに変換してサーバーに渡したい
	
	//録音位置情報緯度 //位置情報についてはgeometry型で保持したかったが、未検証のため
	private Double latitude;
	
    //録音位置情報経度 //位置情報についてはgeometry型で保持したかったが、未検証のため
	private Double longitude;
	
	//評価
	private Integer rating;
	
}
