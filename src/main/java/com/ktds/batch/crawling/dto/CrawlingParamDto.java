package com.ktds.batch.crawling.dto;

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
public class CrawlingParamDto {
    // 상세기능
    private String korNm;
    private String engNm;
    private String paramSize;
    private String required;
    private String sampleData;
    private String paramDesc;
}
