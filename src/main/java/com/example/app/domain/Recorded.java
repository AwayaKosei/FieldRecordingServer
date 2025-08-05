package com.example.app.domain;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Recorded {
	//ID（int 自動生成）
	private Integer recordId;
	
	//タイトル(音源ファイル名)
	private String title;
	
	//録音データ
  private byte[] record;//TODO 処理が重いようであればストレージに移行、後で判断する
	
	//録音者
	private Integer userId;
	
	//録音日時
	private LocalDateTime recordAt;//TODO UTCに変換してサーバーに渡したい
	
	//録音位置情報緯度 //位置情報についてはgeometry型で保持したかったが、未検証のため
	private double latitude;
	
  //録音位置情報経度 //位置情報についてはgeometry型で保持したかったが、未検証のため
	private double longitude;

	

}
