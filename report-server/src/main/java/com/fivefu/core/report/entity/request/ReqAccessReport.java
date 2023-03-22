package com.fivefu.core.report.entity.request;

import com.alibaba.fastjson.JSONArray;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ApiModel("访问报表时的参数")
public class ReqAccessReport implements Serializable {


    @ApiModelProperty(value = "报表实例的id",example = "1" ,required = true)
    private String id;//报表的key

    @ApiModelProperty(value = "报表的请求参数",required = true)
    private String param;

    @ApiModelProperty(value = "导表导出类型；默认为html，值有(pdf,xls,csv,html,docx)",required = true)
    private String exportType;//导入类型；html,pdf,excel


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public String getExportType() {
        return exportType;
    }

    public void setExportType(String exportType) {
        this.exportType = exportType;
    }

}
