package com.example.core.report.controller;
import java.time.LocalDateTime;


import com.example.base.common.utils.StrUtils;
import com.example.base.web.vo.ResultInfo;
import com.example.core.module.auth.utils.SecurityUtil;
import com.example.core.module.auth.vo.SysAuthUser;
import com.example.core.report.anno.LogOption;
import com.example.core.report.constant.BusinessType;
import com.example.core.report.entity.TReportBusiness;
import com.example.core.report.entity.TReportIns;
import com.example.core.report.service.TReportBusinessService;
import com.example.core.report.util.RedisUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    TReportBusinessService tReportBusinessService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "businessName", value = "业务名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pid", value = "父业务id", required = false, dataType = "String"),
    })
    @ApiOperation(value = "新增修改业务",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/add")
    @LogOption(title = "新增修改业务",businessType = BusinessType.INSERT)
    public ResultInfo add(){
        try {
            SysAuthUser sysAuthUser = SecurityUtil.currentUser();
            String businessName = request.getParameter("businessName");
            String id = request.getParameter("id");
            String pid = request.getParameter("pid");
            if(StrUtils.isNull(businessName)){
                return ResultInfo.renderError("业务名称不能为空");
            }
            TReportBusiness tReportBusiness=new TReportBusiness();
            tReportBusiness.setBusinessName(businessName);
            if(ObjectUtils.isNotEmpty(pid)){
                tReportBusiness.setPid(Long.valueOf(pid));
            }
            if(ObjectUtils.isNotEmpty(id)){
                tReportBusiness.setCreatedBy(String.valueOf(sysAuthUser.getUserId()));
                tReportBusiness.setCreatedTime(LocalDateTime.now());
            }else{
                tReportBusiness.setUpdatedBy(String.valueOf(sysAuthUser.getUserId()));
                tReportBusiness.setUpdatedTime(LocalDateTime.now());
            }
            return tReportBusinessService.saveReport(tReportBusiness);
        }catch (Exception e){
            return ResultInfo.renderError("新增修改业务异常");
        }
    }


    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = false, dataType = "String"),
    })
    @ApiOperation(value = "删除业务",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/delete")
    @LogOption(title = "删除业务",businessType = BusinessType.DELETE)
    public ResultInfo delete(){
        try {
            String id = request.getParameter("id");
            if(ObjectUtils.isEmpty(id)){
                return ResultInfo.renderError("id不能为空");
            }
            tReportBusinessService.removeById(id);
            return ResultInfo.renderSuccess();
        }catch (Exception e){
            return ResultInfo.renderError("删除业务异常");
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = false, dataType = "String"),
    })
    @ApiOperation(value = "查询业务树",httpMethod = "GET",response = TReportIns.class)
    @GetMapping("/getTree")
    @LogOption(title = "查询业务树",businessType = BusinessType.OTHER)
    public ResultInfo getTree(){
        try {
            String id = request.getParameter("id");
            //先检查缓存中是否有这条数据

            if(StrUtils.isEmpty(id)){
                return tReportBusinessService.getTree(null);
            }else{
                return tReportBusinessService.getTree(Long.valueOf(id));
            }
        }catch (Exception e){
            return ResultInfo.renderError("业务异常");
        }
    }


}

