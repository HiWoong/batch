package com.ktds.batch.crawling.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CrawlingDetailInfoDto {
    private Map<String, String> swaggerInfo;            // swaggerUrl, swaggerJson

    private Map<String, String> useInfo;                // 상세기능 활용 설명
    private List<CrawlingParamDto> requestParam;        // 상세기능 > 요청 파라미터
    private List<CrawlingParamDto> responseParam;       // 상세기능 > 응답 파라미터
    private Map<String, String> sampleCode;             // 샘플코드
}
