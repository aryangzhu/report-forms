package com.example.core.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.base.web.vo.ResultInfo;
import com.example.core.report.entity.TReportBusiness;
import com.example.core.report.mapper.TReportBusinessMapper;
import com.example.core.report.service.TReportBusinessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务表 服务实现类
 * </p>
 *
 * @author liulei
 * @since 2023-03-09
 */
@Service
public class TReportBusinessServiceImpl extends ServiceImpl<TReportBusinessMapper, TReportBusiness> implements TReportBusinessService {

    private final static Logger logger= LoggerFactory.getLogger(TReportBusinessServiceImpl.class);

    @Override
    public ResultInfo saveReport(TReportBusiness tReportBusiness) {
        try{
            saveOrUpdate(tReportBusiness);
            return ResultInfo.renderSuccess(tReportBusiness.getId());
        }catch (Exception e){
            logger.error("新增异常",e);
            return ResultInfo.renderError("新增异常");
        }
    }

    @Override
    public ResultInfo getTree(Long id) {
        try{
            LambdaQueryWrapper<TReportBusiness> queryWrapper=new LambdaQueryWrapper<>();
            if(ObjectUtils.isNotEmpty(id)){
                queryWrapper.eq(TReportBusiness::getId,id);
            }
            queryWrapper.eq(TReportBusiness::getIsDelete,0)
                    .isNull(TReportBusiness::getPid);
            List<TReportBusiness> list = list(queryWrapper);
            for (TReportBusiness tReportBusiness:list) {
                getTransTree(tReportBusiness);
            }
            return ResultInfo.renderSuccess(list);
        }catch (Exception e){
            logger.error("查询异常",e);
            return ResultInfo.renderError("查询异常");
        }
    }


    public void  getTransTree(TReportBusiness tReportBusiness){
        LambdaQueryWrapper<TReportBusiness> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(TReportBusiness::getPid,tReportBusiness.getId())
                .eq(TReportBusiness::getIsDelete,0)
                .orderByDesc(TReportBusiness::getUpdatedTime);
        List<TReportBusiness> list = list(queryWrapper);
        if(ObjectUtils.isEmpty(list)||list.size()==0){
            return;
        }else{
            tReportBusiness.setChildren(list);
            for (TReportBusiness tReportBusiness1:list) {
                getTransTree(tReportBusiness1);
            }
        }
    }
}
