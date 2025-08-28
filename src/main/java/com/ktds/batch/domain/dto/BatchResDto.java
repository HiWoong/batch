package com.ktds.batch.domain.dto;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BatchResDto {
    private String id; // ID
    private String nm; // 이름
    private String jobClassNm; // 실행 클래스 이름
    private String triggerNm; // 트리거 이름
    private String cronExpression; // 크론 표현식
    private String sttus; // 배치 트리거 상태
    private String description; // 배치 상세 정보
    private String content; // Job의 실제 실행 내용
    private LocalDateTime lastExecutionTime; // 마지막 실행 시각
    private LocalDateTime nextExecutionTime; // 다음 실행 시각
}
