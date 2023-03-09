package com.fivefu.core.report.controller;


import com.fivefu.base.common.utils.StrUtils;
import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.common.BaseController;
import com.fivefu.core.report.constant.BusinessType;
import com.fivefu.core.report.entity.TReportBusiness;
import com.fivefu.core.report.entity.request.ReqReportDataSource;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 业务表 前端控制器
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@Api(tags="业务表")
@RestController
@RequestMapping("/t-report-business")
public class TReportBusinessController extends BaseController {

    @ApiImplicitParams({
            @ApiImplicitParam(name = "businessName", value = "业务名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "父业务名称", required = false, dataType = "String")
    })
    @ApiOperation(value = "新增修改业务树",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/add")
    @LogOption(title = "新增数据源",businessType = BusinessType.INSERT)
    public ResultInfo add(){
        try {
            String businessName = request.getParameter("businessName");
            if(StrUtils.isNull(businessName)){
                return ResultInfo.renderError("业务名称不能为空");
            }
            TReportBusiness tReportBusiness=new TReportBusiness();

            return ResultInfo.renderSuccess();
        }catch (Exception e){
            return ResultInfo.renderError("新增数据源异常");
        }
    }
}

