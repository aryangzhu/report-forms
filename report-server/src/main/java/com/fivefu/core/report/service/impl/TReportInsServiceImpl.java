package com.fivefu.core.report.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fivefu.base.common.utils.date.DateUtils;
import com.fivefu.base.fileupload.minio.services.MinioServices;
import com.fivefu.base.web.vo.ResultInfo;
import com.fivefu.core.module.auth.utils.SecurityUtil;
import com.fivefu.core.module.auth.vo.SysAuthUser;
import com.fivefu.core.report.config.FileServerConfig;
import com.fivefu.core.report.entity.TReportIns;
import com.fivefu.core.report.mapper.TReportInsMapper;
import com.fivefu.core.report.service.TReportInsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fivefu.core.report.util.FileUploadUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.units.qual.A;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;

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

    private final static Logger logger= LoggerFactory.getLogger(TReportInsServiceImpl.class);

    @Autowired
    MinioServices minioService;


    @Autowired
    FileServerConfig fileServerConfig;

    @Value(value = "ffbase.fileupload.path")
    private String path;


    @Override
    public ResultInfo saveReport(TReportIns tReportIns) {
        SysAuthUser sysAuthUser = SecurityUtil.currentUser();
        if(ObjectUtils.isEmpty(sysAuthUser)){
            return ResultInfo.renderError("userid为空");
        }
        if(ObjectUtils.isNotEmpty(tReportIns.getId())){
            tReportIns.setCreatedBy(String.valueOf(sysAuthUser.getUserId()));
            tReportIns.setCreatedTime(LocalDateTime.now());
        }else{
            tReportIns.setUpdatedBy(String.valueOf(sysAuthUser.getUserId()));
            tReportIns.setUpdatedTime(LocalDateTime.now());
        }
        saveOrUpdate(tReportIns);
        //异步解析文件并对文件路径进行替换
        parseFile(tReportIns.getFileUrl(),tReportIns);
        return ResultInfo.renderSuccess();
    }

    @Override
    public ResultInfo getPage(LambdaQueryWrapper<TReportIns> queryWrapper, Page<TReportIns> pageBean) {
        try{
            Page<TReportIns> page = page(pageBean, queryWrapper);
            return ResultInfo.renderSuccess(page);
        }catch (Exception e){
            logger.error("异常",e);
            return ResultInfo.renderError("异常");
        }
    }


    public void parseFile(String url,TReportIns tReportIns){
        try {
            String filePath = FileUploadUtil.parseZipFile(url, path);
            //打开文件
            File newFile = new File(filePath);
            //遍历文件
            if (newFile.isDirectory()) {
                File[] files = newFile.listFiles();
                File xmlFile = null;
                //找到xml配置文件
                for (File file : files) {
                    //检查是否为.xml后缀结尾的文件,如果是的话那么就要读取配置
                    if (file.getName().contains(".xml")) {
                        xmlFile = file;
                    }
                }
                if (ObjectUtils.isNotEmpty(xmlFile)) {
                    //使用SAX解析xml文件
                    SAXReader reader = new SAXReader();
                    Document doc = reader.read(xmlFile);
                    //读取指定标签
//                    Iterator<Element> elementIterator=doc.getRootElement().elementIterator("jrxml");
                    Element root = doc.getRootElement();
                    if (root.getName().equals("jrxml")) {
                        Attribute path = root.attribute("name");
                        //找到主报表对应文件上传至服务器
                        File mainReport1 = getMainReport(files, path.getValue());
                        ResultInfo resultInfo1 = uploadFile(mainReport1);
                        String mainPath = resultInfo1.getMsg();
                        //更新文件路径
                        tReportIns.setFileUrl(mainPath);
                        saveOrUpdate(tReportIns);
                        //获取所有子标签
                        Iterator<Element> elementIterator = root.elementIterator("item");
                        //创建元素集合
//                        ArrayList<Element> list=new ArrayList<>();
                        //使用递归遍历所有元素
                        List<TReportIns> subList=new ArrayList<>();
                        while (elementIterator.hasNext()) {
                            Element item = elementIterator.next();
                            //上传至服务器
                            Attribute attribute1 = item.attribute("name");
                            //找到主报表对应文件上传至服务器
                            File subReport = getMainReport(files, attribute1.getValue());
                            ResultInfo resultInfo2 = uploadFile(subReport);
                            String subPath = resultInfo2.getMsg();
                            //将主报表添加定义表中
                            TReportIns tReportIns1=new TReportIns();
                            tReportIns1.setCreatedBy("");
                            tReportIns1.setCreatedTime(LocalDateTime.now());
                            tReportIns1.setUpdatedBy("");
                            tReportIns1.setUpdatedTime(LocalDateTime.now());
                            tReportIns1.setName(String.valueOf(UUID.randomUUID()));
                            tReportIns1.setDatasourceId(tReportIns.getDatasourceId());
                            tReportIns1.setFileUrl(subPath);
                            tReportIns1.setBusinessId(tReportIns.getBusinessId());
                            tReportIns1.setPid(tReportIns.getId());
                            subList.add(tReportIns1);
                        }
                        //批量插入
                        saveOrUpdateBatch(subList);
                    }
                }
            }
        }catch (Exception e){
            logger.error("压缩包解析异常",e);
        }
    }



    public File getMainReport(File[] files,String name){
        for (int i = 0; i < files.length; i++) {
            if(files[i].getName().equals(name)){
                return files[i];
            }
        }
        return null;
    }



    /**
     * 解压后的文件上传
     */
    public ResultInfo uploadFile(File file){
        try {
//            String path = fileServerConfig.getPathprefix() + DateUtils.getYear(LocalDateTime.now())+"/"+DateUtils.getMonth(LocalDateTime.now())+"/"
//                    +DateUtils.getDay(LocalDateTime.now())+"/"+DateUtils.getHour(LocalDateTime.now())+"/"+DateUtils.getMinutes(LocalDateTime.now())+"/";
//            InputStream inputStream=new FileInputStream(file);
            String resultPath = minioService.uploadFile((MultipartFile) file);
            ResultInfo resultInfo =  ResultInfo.renderSuccess(resultPath);
            resultInfo.setCode(0);
            //文件上传之后进行删除
            FileUploadUtil.delete(file.getName());
            return resultInfo;
        }catch (Exception e){
            logger.error("文件上传出现异常：{}",e);
            return new ResultInfo(false,-1,"异常");
        }

    }

}
