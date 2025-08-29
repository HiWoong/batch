package com.ktds.batch.jobs.crawling.dto;

import java.util.List;
import java.util.Map;

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
public class CrawlingResDto {
    private String dataType;    // 파일데이터, 오픈 API 구분
    private String title;
    private String desc;

    private Map<String, String> metaData;
    private Map<String, String> datasetInfo;
    private List<CrawlingDetailRes> detail;
}
