package com.example.core.report.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import java.io.Serializable;

/**
 * <p>
 * 调用服务日志表
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@TableName("t_sys_log")
public class TSysLog implements Serializable {

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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 反序列化
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 序列化
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
    @JsonDeserialize(using = LocalDateTimeDeserializer.class) // 反序列化
    @JsonSerialize(using = LocalDateTimeSerializer.class) // 序列化
    private LocalDateTime updatedTime;

    /**
     * 删除标记
     */
    @TableField("is_delete")
    private Integer isDelete;

    /**
     * 调用方id
     */
    @TableField("invoke_id")
    private Long invokeId;

    /**
     * 调用方名称
     */
    @TableField("invoke_name")
    private String invokeName;

    /**
     * 调用服务名称
     */
    @TableField("business")
    private String business;


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

    public Long getInvokeId() {
        return invokeId;
    }

    public void setInvokeId(Long invokeId) {
        this.invokeId = invokeId;
    }

    public String getInvokeName() {
        return invokeName;
    }

    public void setInvokeName(String invokeName) {
        this.invokeName = invokeName;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    @Override
    public String toString() {
        return "TSysLog{" +
        "id=" + id +
        ", createdBy=" + createdBy +
        ", createdTime=" + createdTime +
        ", updatedBy=" + updatedBy +
        ", updatedTime=" + updatedTime +
        ", isDelete=" + isDelete +
        ", invokeId=" + invokeId +
        ", invokeName=" + invokeName +
        ", business=" + business +
        "}";
    }
}
