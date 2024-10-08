package com.example.core.report.service;

import com.example.base.web.vo.ResultInfo;
import com.example.core.report.entity.TReportBusiness;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 业务表 服务类
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
public interface TReportBusinessService extends IService<TReportBusiness> {

    ResultInfo saveReport(TReportBusiness tReportBusiness);

    ResultInfo getTree(Long pid);
}
