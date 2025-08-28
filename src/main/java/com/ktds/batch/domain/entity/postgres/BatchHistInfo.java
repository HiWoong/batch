package com.ktds.batch.domain.entity.postgres;

import java.time.LocalDateTime;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "BATCH_HIST_INFO")
public class BatchHistInfo {

    @Id
    @org.springframework.data.annotation.Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id; // ID
    @Column(name = "JOB_CLASS_NM")
    private String jobClassNm; // 실행 클래스 이름
    @Column(name = "EXECUTION_TIME")
    private LocalDateTime executionTime; // 실행 시간
    private String sttus; // 상태
    private String description; // 설명

}
