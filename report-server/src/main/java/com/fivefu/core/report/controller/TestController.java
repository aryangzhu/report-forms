package com.fivefu.core.report.controller;


import com.fivefu.base.web.vo.ResultInfo;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "测试")
@RestController
public class TestController extends BaseController {


    @GetMapping("/test")
    public String test(){
        return request.getRequestURL().toString();
    }
}
