package com.ktds.batch.jobs.crawling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.ktds.batch.jobs.crawling.dto.CrawlingResDto;
import com.ktds.batch.jobs.crawling.service.CrawlingService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class CrawlingController {

    private final CrawlingService crawlingService;

    @GetMapping("/getList")
    public String getOpenApiList() {
        return crawlingService.getOpenAPIList();
    }

    @GetMapping("/getSelenium")
    public CrawlingResDto getSelenium(@RequestParam String url) {
        return crawlingService.getSelenium(url);
    }

}
