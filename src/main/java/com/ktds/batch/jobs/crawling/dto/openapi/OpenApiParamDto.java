package com.ktds.batch.jobs.crawling.dto.openapi;

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
public class OpenApiParamDto {
    // 상세기능
    private String korNm;
    private String engNm;
    private String paramSize;
    private String required;
    private String sampleData;
    private String paramDesc;
}
