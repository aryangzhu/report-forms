package com.example.core.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.base.web.vo.ResultInfo;
import com.example.core.report.entity.TReportIns;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.core.report.entity.request.ReqAccessReport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

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

    ResultInfo getPage(LambdaQueryWrapper<TReportIns> queryWrapper, Page<TReportIns> pageBean);


    public void accessReportHtml(ReqAccessReport reqAccessReport, HttpServletRequest request, HttpServletResponse httpServletResponse);
}
