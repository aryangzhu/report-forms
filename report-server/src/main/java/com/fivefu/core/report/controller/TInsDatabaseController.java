package com.fivefu.core.report.controller;


import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.report.anno.LogOption;
import com.fivefu.core.report.constant.BusinessType;
import com.fivefu.core.report.entity.request.ReqReportDataSource;
import com.fivefu.core.report.service.TInsDatabaseService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 数据库实例 前端控制器
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@RestController
@RequestMapping("/t-ins-database")
public class TInsDatabaseController {


    @Autowired
    TInsDatabaseService tInsDatabaseService;

    @ApiOperation(value = "新增修改数据源",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/add")
    @LogOption(title = "新增数据源",businessType = BusinessType.INSERT)
    public ResultInfo add(@RequestBody ReqReportDataSource reqDatasources){
        try {
            tInsDatabaseService.add(reqDatasources);
            return ResultInfo.renderSuccess();
        }catch (Exception e){
            return ResultInfo.renderError("新增数据源异常");
        }
    }

    @ApiOperation(value = "删除数据源",httpMethod = "GET",response = ResultInfo.class)
    @GetMapping("/remove")
    @LogOption(title = "删除数据源",businessType = BusinessType.INSERT)
    public ResultInfo remove(Long id){
        try {
            tInsDatabaseService.removeById(id);
            return ResultInfo.renderSuccess();
        }catch (Exception e){
            return ResultInfo.renderError("新增数据源异常");
        }
    }

}

