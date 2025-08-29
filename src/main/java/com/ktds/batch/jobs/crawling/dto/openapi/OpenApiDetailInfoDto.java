package com.ktds.batch.jobs.crawling.dto.openapi;

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
public class OpenApiDetailInfoDto {
    private Map<String, String> swaggerInfo;            // swaggerUrl, swaggerJson

    private Map<String, String> useInfo;                // 상세기능 활용 설명
    private List<OpenApiParamDto> requestParam;        // 상세기능 > 요청 파라미터
    private List<OpenApiParamDto> responseParam;       // 상세기능 > 응답 파라미터
    private Map<String, String> sampleCode;             // 샘플코드
}
