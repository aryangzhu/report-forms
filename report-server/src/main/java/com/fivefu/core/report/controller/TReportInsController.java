package com.fivefu.core.report.controller;


import com.fivefu.base.common.utils.StrUtils;
import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.module.auth.utils.SecurityUtil;
import com.fivefu.core.module.auth.vo.SysAuthUser;
import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.constant.BusinessType;
import com.fivefu.core.report.entity.TReportIns;
import com.fivefu.core.report.service.TReportInsService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

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
            @ApiImplicitParam(name = "businessid", value = "业务id", required = false, dataType = "String")
    })
    @ApiOperation(value = "新增修改业务",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/add")
    @LogOption(title = "新增修改业务",businessType = BusinessType.INSERT)
    public ResultInfo add(){
        try {
            SysAuthUser sysAuthUser = SecurityUtil.currentUser();
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
            tReportIns.setDatasourceId(0L);
            tReportIns.setFileUrl("");
            tReportIns.setParam("");
            tReportIns.setBusinessId(0L);
            if(ObjectUtils.isNotEmpty(id)){
                tReportIns.setCreatedBy(String.valueOf(sysAuthUser.getUserId()));
                tReportIns.setCreatedTime(LocalDateTime.now());
            }else{
                tReportIns.setUpdatedBy(String.valueOf(sysAuthUser.getUserId()));
                tReportIns.setUpdatedTime(LocalDateTime.now());
            }
            return tReportInsService.saveReport(tReportIns);
        }catch (Exception e){
            return ResultInfo.renderError("新增修改业务异常");
        }
    }

}

