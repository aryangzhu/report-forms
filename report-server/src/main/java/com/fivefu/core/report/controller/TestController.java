package com.fivefu.core.report.controller;

import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.constant.BusinessType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @LogOption(title = "测试",businessType = BusinessType.OTHER)
    @RequestMapping("test")
    public String test(){
        return "test success";
    }
}
