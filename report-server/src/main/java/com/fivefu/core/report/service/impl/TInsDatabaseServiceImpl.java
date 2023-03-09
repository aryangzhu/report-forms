package com.fivefu.core.report.service.impl;

import com.fivefu.core.report.entity.TInsDatabase;
import com.fivefu.core.report.entity.request.ReqDatabases;
import com.fivefu.core.report.entity.request.ReqReportDataSource;
import com.fivefu.core.report.exception.FFNullException;
import com.fivefu.core.report.mapper.TInsDatabaseMapper;
import com.fivefu.core.report.service.TInsDatabaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * <p>
 * 数据库实例 服务实现类
 * </p>
 *
 * @author liulei
 * @since 2023-03-08
 */
@Service
public class TInsDatabaseServiceImpl extends ServiceImpl<TInsDatabaseMapper, TInsDatabase> implements TInsDatabaseService {

    @Transactional(propagation= Propagation.REQUIRED)
    @Override
    public void add(ReqReportDataSource reqDatasources) {
        String userId=reqDatasources.getUserid();
        if(reqDatasources == null){
            throw new FFNullException("参数不能为空");
        }
        if(reqDatasources.getType()==null && reqDatasources.getType()==0){
            throw new FFNullException("请检查数据源类型是否正确");
        }

        TInsDatabase tInsDatabase=new TInsDatabase();
        BeanUtils.copyProperties(reqDatasources,tInsDatabase);
        BeanUtils.copyProperties(reqDatasources.getReqDatabases(),tInsDatabase);
        if(ObjectUtils.isEmpty(reqDatasources.getId())){
            tInsDatabase.setCreatedBy(userId);
            tInsDatabase.setUpdatedTime(LocalDateTime.now());
            tInsDatabase.setCreatedBy(userId);
            tInsDatabase.setUpdatedTime(LocalDateTime.now());
        }else{
            tInsDatabase.setUpdatedBy(userId);
            tInsDatabase.setUpdatedTime(LocalDateTime.now());
        }
        saveOrUpdate(tInsDatabase);
    }

}
