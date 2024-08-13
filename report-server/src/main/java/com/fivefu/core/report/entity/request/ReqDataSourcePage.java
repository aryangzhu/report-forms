package com.example.core.report.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("列表查询数据源实体类")
public class ReqDataSourcePage {

    @ApiModelProperty(value = "数据源类型",example = "1" ,required = false)
    private Long datasourceType;
    @ApiModelProperty(value = "数据源名称",example = "1" ,required = false)
    private String name;

    @ApiModelProperty(value = "当前页",example = "1" )
    private int page;
    @ApiModelProperty(value = "每页大小",example = "10" )
    private int limit;

    public Long getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Long datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
