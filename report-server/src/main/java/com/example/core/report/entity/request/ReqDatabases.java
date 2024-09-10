package com.example.core.report.entity.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("数据源实体")
public class ReqDatabases implements Serializable {
    /**
     * 数据库类型
     */
    @ApiModelProperty(value = "数据库类型",example = "1" ,required = true)
    private Long datasourceType;

    /**
     * 驱动类名称
     */
    @ApiModelProperty(value = "驱动类名称",example = "com.mysql.cj.jdbc.Driver" ,required = true)
    private String driverClassName;

    /**
     * URL地址
     */
    @ApiModelProperty(value = "URL地址",example = "jdbc://xxxxx:3306/test" ,required = true)
    private String url;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "数据库用户名",example = "test" ,required = true)
    private String username;

    /**
     * 密码
     */
    @ApiModelProperty(value = "数据库密码",example = "test" ,required = true)
    private String password;

    /**
     * 数据库名称
     */
    @ApiModelProperty(value = "xxx系统数据库",example = "xxx系统数据库" ,required = true)
    private String name;


    public Long getDatasourceType() {
        return datasourceType;
    }

    public void setDatasourceType(Long datasourceType) {
        this.datasourceType = datasourceType;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
