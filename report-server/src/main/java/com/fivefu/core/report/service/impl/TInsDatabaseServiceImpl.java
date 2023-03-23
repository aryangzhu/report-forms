package com.fivefu.core.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fivefu.base.common.utils.date.DateUtils;
import com.fivefu.base.common.utils.str.StrUtils;
import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.report.entity.ResDatasource;
import com.fivefu.core.report.entity.TInsDatabase;
import com.fivefu.core.report.entity.request.ReqDataSourcePage;
import com.fivefu.core.report.entity.request.ReqDatabases;
import com.fivefu.core.report.entity.request.ReqReportDataSource;
import com.fivefu.core.report.exception.FFNullException;
import com.fivefu.core.report.mapper.TInsDatabaseMapper;
import com.fivefu.core.report.service.TInsDatabaseService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fivefu.module.dictionary.entity.DbSysDict;
import com.fivefu.module.dictionary.service.DbSysDictService;
import com.fivefu.module.dictionary.service.impl.DbSysDictServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.awt.image.DataBufferShort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    Map<Long,String> cacheMap=new HashMap<>();

//    @Autowired
//    DbSysDictService dbSysDictService;


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

    @Override
    public ResultInfo listByPage(ReqDataSourcePage reqDataSourcePage) {
//        DbSysDictServiceImpl dbSysDictService=new DbSysDictServiceImpl();
        LambdaQueryWrapper<DbSysDict> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DbSysDict::getIsDelete,0)
                .eq(DbSysDict::getDictDesc,"datasourceType");
//        List<DbSysDict> list = dbSysDictService.list(queryWrapper);
//        cacheMap = list.stream().collect(Collectors.toMap(DbSysDict::getId, DbSysDict::getDictName));
        if(reqDataSourcePage==null){
            throw new FFNullException("请检查参数是否为空");
        }
        QueryWrapper<TInsDatabase> tReportDatasoucesQueryWrapper = new QueryWrapper<TInsDatabase>();
        if(reqDataSourcePage.getDatasourceType() !=null && reqDataSourcePage.getDatasourceType()!= 0){
            tReportDatasoucesQueryWrapper.eq("database_type",reqDataSourcePage.getDatasourceType());
        }

        if(StrUtils.isNotNull(reqDataSourcePage.getName())){
            tReportDatasoucesQueryWrapper.like("name_",reqDataSourcePage.getName());
        }
        //排序
        tReportDatasoucesQueryWrapper.orderByDesc("updated_time");

        Page<TInsDatabase> pageBean = new Page<TInsDatabase>(reqDataSourcePage.getPage(),reqDataSourcePage.getLimit());
        Page<TInsDatabase> tDefDatasourcePage = getBaseMapper().selectPage(pageBean,tReportDatasoucesQueryWrapper);
        //响应
        List<TInsDatabase> datasoucesList = tDefDatasourcePage.getRecords();
        Long count = tDefDatasourcePage.getTotal();


        //构造响应
        List<ResDatasource> resDatasourceList = new ArrayList<ResDatasource>();
        datasoucesList.stream().forEach(tReportDatasouces -> {
            ResDatasource resDatasource = new ResDatasource();
            resDatasource.setId(tReportDatasouces.getId());
            resDatasource.setCreatedBy(tReportDatasouces.getCreatedBy());
            resDatasource.setCreatedTime(DateUtils.format(tReportDatasouces.getCreatedTime(),DateUtils.DEFAULT_DATETIME_PATTERN));
            resDatasource.setUpdatedBy(tReportDatasouces.getUpdatedBy());
            resDatasource.setUpdatedTime(DateUtils.format(tReportDatasouces.getUpdatedTime(),DateUtils.DEFAULT_DATETIME_PATTERN));
            resDatasource.setDatasourceType(tReportDatasouces.getDatasourceType());
//            TDataDic tDataDic = tDataDicService.getById(tReportDatasouces.getType());
//            resDatasource.setDatasourceTypeName(cache.getOrDefault(tReportDatasouces.getId(),""));
            resDatasource.setName(tReportDatasouces.getName());
            String s = cacheMap.getOrDefault(tReportDatasouces.getId(),"");
//            resDatasource.setInsDatasourceId(s);
            resDatasource.setDatasourceTypeName(s);
            resDatasourceList.add(resDatasource);
        });
        ResultInfo<List<ResDatasource>> resultInfo = ResultInfo.renderSuccess(resDatasourceList);
        resultInfo.setCount(count);
        return resultInfo;
    }

    @Override
    public ResultInfo listSelect() {
        LambdaQueryWrapper<TInsDatabase> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.select(TInsDatabase::getId,TInsDatabase::getName)
                .eq(TInsDatabase::getIsDelete,0);
        List<TInsDatabase> list = list(queryWrapper);
        return ResultInfo.renderSuccess(list);
    }

}
