package com.ktds.batch.crawling.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import com.ktds.batch.crawling.dto.CrawlingResDto;
import com.ktds.batch.crawling.service.DemoService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1")
public class DemoController {

    private final DemoService demoService;

    @GetMapping("/getHello")
    public String getApiHstList() {
        return demoService.getHello();
    }

    @GetMapping("/getList")
    public String getApiList() {
        return demoService.getOpenAPIList();
    }

    @GetMapping("/getSelenium")
    public CrawlingResDto getSelenium(@RequestParam String url) {
        return demoService.getSelenium(url);
    }

}
