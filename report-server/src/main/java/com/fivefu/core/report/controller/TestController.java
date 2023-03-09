package com.fivefu.core.report.controller;

import com.fivefu.core.report.anno.LogOption;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @LogOption
    @RequestMapping("test")
    public String test(){

        return "test success";
    }
}
