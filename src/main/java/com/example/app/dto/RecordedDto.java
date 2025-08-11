//src/main/java/com/example/app/dto/RecordedDto.java
package com.example.app.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RecordedDto {
   private Integer recordId;
   private Integer userId;
   private String title;
   private Double latitude;
   private Double longitude;
   private LocalDateTime recordAt;
   private String fileUrl; // ← 追加（/api/records/{id}/file）
}