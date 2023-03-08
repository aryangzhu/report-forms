package com.fivefu.core.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

/**
 * <p>
 * 数据库实例
 * </p>
 *
 * @author liulei
 * @since 2023-03-08
 */
@TableName("t_ins_database")
public class TInsDatabase implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private LocalDateTime createdTime;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 更新时间
     */
    @TableField("updated_time")
    private LocalDateTime updatedTime;

    /**
     * 删除标记
     */
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * 数据库类型
     */
    @TableField("datasource_type")
    private Long datasourceType;

    /**
     * 驱动类名称
     */
    @TableField("driver_class_name")
    private String driverClassName;

    /**
     * URL地址
     */
    @TableField("url_")
    private String url;

    /**
     * 用户名
     */
    @TableField("username_")
    private String username;

    /**
     * 密码
     */
    @TableField("password_")
    private String password;

    /**
     * 数据库名称
     */
    @TableField("name_")
    private String name;

    /**
     * 请求方法类型
     */
    @TableField("method_type")
    private String methodType;

    /**
     * 请求头(包含token,否则无法调用其他服务)
     */
    @TableField("method_header")
    private String methodHeader;

    /**
     * 请求体
     */
    @TableField("method_body")
    private String methodBody;


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

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    public Integer getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

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

    public String getMethodBody() {
        return methodBody;
    }

    public void setMethodBody(String methodBody) {
        this.methodBody = methodBody;
    }

    @Override
    public String toString() {
        return "TInsDatabase{" +
        "id=" + id +
        ", createdBy=" + createdBy +
        ", createdTime=" + createdTime +
        ", updatedBy=" + updatedBy +
        ", updatedTime=" + updatedTime +
        ", isDelete=" + isDelete +
        ", datasourceType=" + datasourceType +
        ", driverClassName=" + driverClassName +
        ", url=" + url +
        ", username=" + username +
        ", password=" + password +
        ", name=" + name +
        ", methodType=" + methodType +
        ", methodHeader=" + methodHeader +
        ", methodBody=" + methodBody +
        "}";
    }
}
