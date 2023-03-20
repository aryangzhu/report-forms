package com.fivefu.core.report.service.impl;

import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.report.entity.TReportIns;
import com.fivefu.core.report.mapper.TReportInsMapper;
import com.fivefu.core.report.service.TReportInsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 报表实例表 服务实现类
 * </p>
 *
 * @author liulei
 * @since 2023-03-20
 */
@Service
public class TReportInsServiceImpl extends ServiceImpl<TReportInsMapper, TReportIns> implements TReportInsService {

    @Override
    public ResultInfo saveReport(TReportIns tReportIns) {
        return null;
    }
}
