package com.dekapx.apps.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.dekapx.apps.common.ResourceUrls.BASE_URL;
import static com.dekapx.apps.common.ResourceUrls.INFO_URL;

@Slf4j
@RestController
@RequestMapping(BASE_URL)
@RequiredArgsConstructor
public class TestController {
    @GetMapping(value = INFO_URL, version = "1.0")
    public String getInfoV1() {
        log.info("Test Controller v1.0");
        return "Test Controller v1.0";
    }

    @GetMapping(value = INFO_URL, version = "2.0")
    public String getInfoV2() {
        log.info("Test Controller v2.0");
        return "Test Controller v2.0";
    }

}
