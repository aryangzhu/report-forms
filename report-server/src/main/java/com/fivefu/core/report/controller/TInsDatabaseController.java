package com.example.core.report.controller;


import com.example.base.web.vo.ResultInfo;
import com.example.core.report.anno.LogOption;
import com.example.core.report.constant.BusinessType;
import com.example.core.report.entity.ResDatasource;
import com.example.core.report.entity.request.ReqDataSourcePage;
import com.example.core.report.entity.request.ReqReportDataSource;
import com.example.core.report.exception.FFNullException;
import com.example.core.report.service.TInsDatabaseService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据库实例 前端控制器
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@Api(tags="数据源")
@RestController
@RequestMapping("/t-ins-database")
public class TInsDatabaseController extends BaseController {

    private final static Logger logger= LoggerFactory.getLogger(TInsDatabaseController.class);

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


    @ApiOperation(value = "分页查询数据源列表",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/listPage")
    public ResultInfo<List<ResDatasource>> listPage(@RequestBody ReqDataSourcePage reqDataSourcePage){
        try {
            ResultInfo resultInfo = tInsDatabaseService.listByPage(reqDataSourcePage);
            resultInfo.setCode(0);
            return  resultInfo;
        }catch (FFNullException e){
            logger.error("查询列表异常",e);
            return ResultInfo.renderError("查询异常："+e.getMessage());
        }catch (Exception e){
            logger.error("查询列表异常",e);
            return ResultInfo.renderError("查询异常");
        }
    }


    @ApiOperation(value = "数据源下拉框",httpMethod = "POST",response = ResultInfo.class)
    @PostMapping("/listSelect")
    @LogOption(title = "查询数据源",businessType = BusinessType.OTHER)
    public ResultInfo<List<ResDatasource>> listSelect(){
        try {
            ResultInfo resultInfo = tInsDatabaseService.listSelect();
            resultInfo.setCode(0);
            return  resultInfo;
        }catch (FFNullException e){
            logger.error("查询列表异常",e);
            return ResultInfo.renderError("查询异常："+e.getMessage());
        }catch (Exception e){
            logger.error("查询列表异常",e);
            return ResultInfo.renderError("查询异常");
        }
    }

}

