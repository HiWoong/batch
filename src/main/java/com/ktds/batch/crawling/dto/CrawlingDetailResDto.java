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
public class CrawlingDetailResDto {
    private String apiNm;
    private String apiDesc;

    private String edpt;        // 엔드포인트 URL
    private String uri;         // 요청 uri
    private String port;        // 도메인 포트

    private CrawlingDetailInfoDto detailInfo;
}
