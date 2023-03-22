package com.fivefu.core.report.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("请求时的key和value")
public class ReqKeyValue implements Serializable {

    @ApiModelProperty(value = "参数的key",example = "FFReport20220826001" ,required = true)
    private String key;
    @ApiModelProperty(value = "参数的value",example = "FFReport20220826001" ,required = true)
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
