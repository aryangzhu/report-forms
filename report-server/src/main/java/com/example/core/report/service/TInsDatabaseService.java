package com.example.core.report.service;

import com.example.base.web.vo.ResultInfo;
import com.example.core.report.entity.TInsDatabase;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.core.report.entity.request.ReqDataSourcePage;
import com.example.core.report.entity.request.ReqReportDataSource;

/**
 * <p>
 * 数据库实例 服务类
 * </p>
 *
 * @author liulei
 * @since 2023-03-08
 */
public interface TInsDatabaseService extends IService<TInsDatabase> {


    /**
     * 新增数据源
     * @param reqDatasources
     */
    void add(ReqReportDataSource reqDatasources);

    ResultInfo listByPage(ReqDataSourcePage reqDataSourcePage);

    ResultInfo listSelect();
}
