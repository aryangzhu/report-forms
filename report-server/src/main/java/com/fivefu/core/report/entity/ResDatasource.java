package com.fivefu.core.report.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("响应数据源实体")
public class ResDatasource implements Serializable {

    /**
     * 主键
     */
    @ApiModelProperty(value = "主键",example = "1" ,required = true)
    private Long id;

    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人",example = "glm" ,required = true)
    private String createdBy;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间",example = "2022-08-25 16:33:38" ,required = true)
    private String createdTime;

    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人",example = "glm" ,required = true)
    private String updatedBy;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间",example = "2022-08-25 16:33:38" ,required = true)
    private String updatedTime;

    @ApiModelProperty(value = "数据源名称",example = "测试数据源" ,required = true)
    private String name;

    /**
     * 数据库类型
     */
    @ApiModelProperty(value = "数据源类型",example = "1" ,required = true)
    private Long datasourceType;
    //数据库类型名称
    @ApiModelProperty(value = "数据源类型名称",example = "mysql" ,required = true)
    private String datasourceTypeName;

    @ApiModelProperty(value = "数据源实例ID",example = "1" ,required = true)
    private Long insDatasourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Long getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Long datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getDatasourceTypeName() {
        return datasourceTypeName;
    }

    public void setDatasourceTypeName(String datasourceTypeName) {
        this.datasourceTypeName = datasourceTypeName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInsDatasourceId() {
        return insDatasourceId;
    }

    public void setInsDatasourceId(Long insDatasourceId) {
        this.insDatasourceId = insDatasourceId;
    }
}
