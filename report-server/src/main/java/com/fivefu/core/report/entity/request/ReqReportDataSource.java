package com.fivefu.core.report.entity.request;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel("新增报表数据源")
public class ReqReportDataSource implements Serializable {

    private Long id;

    /**
     *
     */
    @ApiModelProperty(value = "数据源名称",example = "测试数据源" ,required = true)
    private String name;

    /**
     * 数据源类型
     */
    @ApiModelProperty(value = "数据源类型",example = "1" ,required = true)
    private Long type;


    @ApiModelProperty(value = "根据数据类型的不同，传递的参数也不同",example = "数据源为数据库的使用此参数")
    private ReqDatabases reqDatabases;

    /**
     * 请求方法类型
     */
    @ApiModelProperty("请求方法类型")
    private String methodType;

    /**
     * 请求头(包含token,否则无法调用其他服务)
     */
    private String methodHeader;

    private String userid;

    /**
     * 请求体
     */
    @TableField("请求体")
    private String methodBody;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getType() {
        return type;
    }

    public void setType(Long type) {
        this.type = type;
    }

    public ReqDatabases getReqDatabases() {
        return reqDatabases;
    }

    public void setReqDatabases(ReqDatabases reqDatabases) {
        this.reqDatabases = reqDatabases;
    }


    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType;
    }

    public String getMethodHeader() {
        return methodHeader;
    }

    public void setMethodHeader(String methodHeader) {
        this.methodHeader = methodHeader;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }
}
