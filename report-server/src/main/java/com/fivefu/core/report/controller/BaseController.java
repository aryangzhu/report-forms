package com.fivefu.core.report.controller;

import com.fivefu.base.common.utils.StrUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class BaseController {

    @Autowired
    public HttpServletRequest request;


    @Autowired
    public HttpServletResponse response;


    public Map<String, String> formatDataToMap() {
        Map<String, String> info = new HashMap<String, String>();
        Map<String, String[]> map = request.getParameterMap();
        map.forEach((key, value) -> {
            info.put(key, StrUtils.isCheckNull(value[0]));
        });
        return info;
    }
}
