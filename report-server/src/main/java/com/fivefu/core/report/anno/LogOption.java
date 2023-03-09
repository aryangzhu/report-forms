package com.fivefu.core.report.anno;


import com.fivefu.core.report.constant.BusinessType;
import com.fivefu.core.report.constant.OperatorType;
import com.fivefu.core.report.entity.TInsDatabase;

import java.lang.annotation.*;


@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOption {

    /**
     * 模块
     */
    public String title() default "";


    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;

    /**
     * 是否保存响应的参数
     */
    public boolean isSaveResponseData() default true;
}
