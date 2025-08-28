package com.ktds.batch.jobs.crawling.dto.openapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import com.ktds.batch.jobs.crawling.dto.CrawlingDetailRes;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class OpenApiDetailResDto implements CrawlingDetailRes {
    private String apiNm;
    private String apiDesc;

    private String edpt;        // 엔드포인트 URL
    private String uri;         // 요청 uri
    private String port;        // 도메인 포트

    private OpenApiDetailInfoDto detailInfo;
}
