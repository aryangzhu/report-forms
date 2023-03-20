package com.fivefu.core.report.service;

import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.report.entity.TReportIns;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 报表实例表 服务类
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
public interface TReportInsService extends IService<TReportIns> {

    ResultInfo saveReport(TReportIns tReportIns);
}
