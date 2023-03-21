package com.fivefu.core.report.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fivefu.base.common.utils.StrUtils;
import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.module.auth.utils.SecurityUtil;
import com.fivefu.core.module.auth.vo.SysAuthUser;
import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.constant.BusinessType;
import com.fivefu.core.report.entity.TReportIns;
import com.fivefu.core.report.service.TReportInsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.print.DocFlavor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * <p>
 * 报表实例表 前端控制器
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@Api(tags = "报表管理")
@RestController
@RequestMapping("/t-report-ins")
public class TReportInsController extends BaseController {



    @Autowired
    TReportInsService tReportInsService;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "name", value = "模板名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "datasourceid", value = "数据源id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "fileUrl", value = "模板路径", required = false, dataType = "String"),
            @ApiImplicitParam(name = "param", value = "参数", required = false, dataType = "String"),
            @ApiImplicitParam(name = "businessid", value = "业务id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "isZip", value = "是否为压缩包", required = false, dataType = "String")

    })
    @ApiOperation(value = "新增修改报表",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/add")
    @LogOption(title = "新增修改报表",businessType = BusinessType.INSERT)
    public ResultInfo add(){
        try {
            Map<String, String> map = formatDataToMap();
            String id = map.get("id");
            String name = map.get("name");
            String datasourceid = map.get("datasourceid");
            String fileUrl = map.get("fileUrl");
            String param = map.get("param");
            String businessid = map.get("businessid");
            if(StrUtils.isNull(name)){
                return ResultInfo.renderError("名称不能为空");
            }
            TReportIns tReportIns = new TReportIns();
            tReportIns.setName(name);
            tReportIns.setDatasourceId(Long.valueOf(datasourceid));
            tReportIns.setFileUrl(fileUrl);
            tReportIns.setParam(param);
            tReportIns.setBusinessId(Long.valueOf(businessid));
            return tReportInsService.saveReport(tReportIns);
        }catch (Exception e){
            return ResultInfo.renderError("新增修改业务异常");
        }
    }



    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "页", required = true, dataType = "String"),
            @ApiImplicitParam(name = "limit", value = "数量", required = true, dataType = "String"),
            @ApiImplicitParam(name = "name", value = "模板名称", required = false, dataType = "String"),
            @ApiImplicitParam(name = "datasourceid", value = "数据源id", required = false, dataType = "String"),
            @ApiImplicitParam(name = "businessid", value = "业务id", required = false, dataType = "String"),
    })
    @ApiOperation(value = "分页查询报表",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/getPage")
    @LogOption(title = "分页查询报表",businessType = BusinessType.INSERT)
    public ResultInfo getPage(){
        Map<String, String> map = formatDataToMap();
        String name = map.get("name");
        String datasourceid = map.get("datasourceid");
        String fileUrl = map.get("fileUrl");
        String page = map.get("page");
        String limit = map.get("limit");
        String businessid = map.get("businessid");
        if(StrUtils.isNull(page)||StrUtils.isNull(limit)){
            return ResultInfo.renderError("缺少必要参数");
        }
        Page<TReportIns> pageBean=new Page<>(Integer.parseInt(page),Integer.parseInt(limit));
        LambdaQueryWrapper<TReportIns> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtils.isNotNull(name),TReportIns::getName,name);
        return tReportInsService.getPage(queryWrapper,pageBean);
    }

}

