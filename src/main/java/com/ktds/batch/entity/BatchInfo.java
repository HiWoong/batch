package com.ktds.batch.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Entity
@Accessors(chain = true)
@Table(name = "BATCH_INFO")
public class BatchInfo {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id; // ID

    private String nm; // 이름
    private String jobClassNm; // 실행 클래스 이름
    private String triggerNm; // 트리거 이름
    private String cronExpression; // 크론 표현식
    private String sttus; // 배치 트리거 상태
    private String description; // 배치 상세 정보
    @Column(columnDefinition = "TEXT")
    private String content; // Job의 실제 실행 내용
    private LocalDateTime lastExecutionTime; // 마지막 실행 시각
    private LocalDateTime nextExecutionTime; // 다음 실행 시각

}
