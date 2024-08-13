package com.example.core.report.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.base.common.utils.StrUtils;
import com.example.base.common.utils.date.DateUtils;
import com.example.base.fileupload.minio.services.MinioServices;
import com.example.base.web.vo.ResultInfo;
import com.example.core.module.auth.utils.SecurityUtil;
import com.example.core.module.auth.vo.SysAuthUser;
import com.example.core.report.config.FileServerConfig;
import com.example.core.report.constant.TypeEnum;
import com.example.core.report.entity.TInsDatabase;
import com.example.core.report.entity.TReportBusiness;
import com.example.core.report.entity.TReportIns;
import com.example.core.report.entity.common.DataSourceBeanFactory;
import com.example.core.report.entity.request.ReqAccessReport;
import com.example.core.report.entity.request.ReqKeyValue;
import com.example.core.report.exception.FFNullException;
import com.example.core.report.mapper.TReportInsMapper;
import com.example.core.report.service.TInsDatabaseService;
import com.example.core.report.service.TReportBusinessService;
import com.example.core.report.service.TReportInsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.core.report.util.CusHttpUtil;
import com.example.core.report.util.FileUploadUtil;
import com.example.module.dictionary.service.DbSysDictService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.j2ee.servlets.ImageServlet;
import net.sf.jasperreports.web.util.WebHtmlResourceHandler;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.checkerframework.checker.units.qual.A;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

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

    Map<Long,TReportBusiness> map1=new HashMap<>();

    Map<Long,TInsDatabase> map2=new HashMap<>();

//    @Autowired
//    MinioServices minioService;


    @Autowired
    FileServerConfig fileServerConfig;


    @Autowired
    TInsDatabaseService tInsDatabaseService;

    @Autowired
    TReportBusinessService tReportBusinessService;

    @Autowired
    DbSysDictService dbSysDictService;

    @Value("${ffbase.fileupload.path}")
    private String path;

    /**
     * 新增
     * @param tReportIns
     * @return
     */
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

    /**
     * 条件分页查询
     * @param queryWrapper
     * @param pageBean
     * @return
     */
    @Override
    public ResultInfo getPage(LambdaQueryWrapper<TReportIns> queryWrapper, Page<TReportIns> pageBean) {
        try{
            //查询业务和数据源
            LambdaQueryWrapper<TReportBusiness> queryWrapper1=new LambdaQueryWrapper<>();
            queryWrapper1.eq(TReportBusiness::getIsDelete,0);
            List<TReportBusiness> list1 = tReportBusinessService.list(queryWrapper1);
            map1.clear();
            map1 = list1.stream().collect(Collectors.toMap(TReportBusiness::getId, t -> t, (k1, k2) -> k1));
            LambdaQueryWrapper<TInsDatabase> queryWrapper2=new LambdaQueryWrapper<>();
            queryWrapper2.eq(TInsDatabase::getIsDelete,0);
            List<TInsDatabase> list2 = tInsDatabaseService.list(queryWrapper2);
            map2.clear();
            map2 = list2.stream().collect(Collectors.toMap(TInsDatabase::getId, t -> t, (k1, k2) -> k1));
            Page<TReportIns> page = page(pageBean, queryWrapper);
            List<TReportIns> records = page.getRecords();
            List<TReportIns> result=new ArrayList<>();
            if(ObjectUtils.isNotEmpty(records)&&records.size()>0){
                result = records.stream().map(tReportIns -> {
                    TReportIns tReportIns1 = tReportIns;
                    tReportIns1.setDatasourceName((ObjectUtils.isNotEmpty(map2.get(tReportIns.getDatasourceId())))?((TInsDatabase) map2.get(tReportIns.getDatasourceId())).getName():"");
                    tReportIns1.setBusinessName((ObjectUtils.isNotEmpty(map1.get(tReportIns.getBusinessId())))?((TReportBusiness) map1.get(tReportIns.getBusinessId())).getBusinessName():"");
                    return tReportIns1;
                }).collect(Collectors.toList());
                page.setRecords(result);
            }
            return ResultInfo.renderSuccess(0,"成功",page.getTotal(),result);
        }catch (Exception e){
            logger.error("异常",e);
            return ResultInfo.renderError("异常");
        }
    }

    /**
     * 解析压缩包,主要是分了区分主报表和子报表
     * @param url
     * @param tReportIns
     */
    public void parseFile(String url,TReportIns tReportIns){
        try {
            String suffix = url.substring(url.lastIndexOf("."));
            byte[] bytes = downloadFileNew(fileServerConfig.getServerip()+url);
            File zipfile=new File(path+"/"+UUID.randomUUID()+suffix);
            FileOutputStream fos=new FileOutputStream(zipfile);
            //将bytes写入File中
            fos.write(bytes,0,bytes.length);
            //解压文件
            String filePath = FileUploadUtil.parseZipFile(zipfile.getPath(), path+"/"+zipfile.getName().substring(0,zipfile.getName().lastIndexOf(".")));
            //打开文件
            File newFile = new File(filePath);
            //遍历文件
            if (newFile.isDirectory()) {
                File[] files = newFile.listFiles();
                File singleFile=null;
                File xmlFile = null;
                //找到xml配置文件
                for (File file : files) {
                    //检查是否为.xml后缀结尾的文件,如果是的话那么就要读取配置
                    if (file.getName().contains(".xml")) {
                        xmlFile = file;
                    }
                    if(file.getName().contains(".jrxml")){
                        singleFile=file;
                    }
                }
                if (ObjectUtils.isNotEmpty(xmlFile)&&ObjectUtils.isEmpty(singleFile)) {
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
                        tReportIns.setJasperFile(mainPath);
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
                            tReportIns1.setCreatedBy("sys");
                            tReportIns1.setCreatedTime(LocalDateTime.now());
                            tReportIns1.setUpdatedBy("sys");
                            tReportIns1.setUpdatedTime(LocalDateTime.now());
                            tReportIns1.setName(String.valueOf(UUID.randomUUID()));
                            tReportIns1.setDatasourceId(tReportIns.getDatasourceId());
                            tReportIns1.setJasperFile(subPath);
                            tReportIns1.setBusinessId(tReportIns.getBusinessId());
                            tReportIns1.setPid(tReportIns.getId());
                            subList.add(tReportIns1);
                        }
                        //批量插入
                        saveOrUpdateBatch(subList);
                    }
                }else{//单表
//                    ResultInfo resultInfo = uploadFile(singleFile);
//                    tReportIns.setJasperFile(resultInfo.getMsg());
                    tReportIns.setJasperFile(singleFile.getPath());
                    saveOrUpdate(tReportIns);
                }
            }
        }catch (Exception e){
            logger.error("压缩包解析异常",e);
            e.printStackTrace();
        }
    }


    /**
     *从多个文件中找到主报表
     * @param files
     * @param name
     * @return
     */
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
            InputStream inputStream=new FileInputStream(file);
            MultipartFile file1= new MockMultipartFile(file.getName(),inputStream);
//            String resultPath = minioService.uploadFile(file1);
            String resultPath="data/temp";
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



    /**
     * 访问报表，以HTML的形式
     * @param reqAccessReport
     * @return
     */
    public void accessReportHtml1(ReqAccessReport reqAccessReport,HttpServletRequest request, HttpServletResponse response) {
        //先找到报表实例
        LambdaQueryWrapper<TReportIns> tInsReportQueryWrapper = new LambdaQueryWrapper<>();
        tInsReportQueryWrapper.eq(TReportIns::getId,reqAccessReport.getId());
        //获取主报表实例
        List<TReportIns> tInsReportList = list(tInsReportQueryWrapper);
        if(tInsReportList ==null || tInsReportList.size()==0){
            throw new FFNullException("未找到要访问的报表");
        }
        if(tInsReportList.size()>1){
            throw new FFNullException("系统异常，出现1个以上相同报表实例");
        }

        //报表实例
        TReportIns tReportIns = tInsReportList.get(0);
        //如果临时文件中没有当前jasper文件，那么将重新下载.文件的命名规则为：key+"_"+id+".jasper"
        File jaserFile = getJasperFile(tReportIns);

        //获取子报表实例
        tInsReportQueryWrapper=new LambdaQueryWrapper<>();
        tInsReportQueryWrapper.eq(TReportIns::getPid,tReportIns.getId());
        List<TReportIns> subInsReportList=list(tInsReportQueryWrapper);
        List<File> fileList=new ArrayList<>();
        //子表文件
        for(TReportIns insReport:subInsReportList){
            File subjapserFile=getJasperFile(insReport);
            fileList.add(subjapserFile);
        }
        //获取报表的数据源
        TInsDatabase tInsDatabase = tInsDatabaseService.getById(tReportIns.getDatasourceId());
        //不同的数据源类型，有不同的处理方式
        String destFileName = fileServerConfig.getTmpPath()+System.currentTimeMillis()+".html";
        if(tInsDatabase.getDatabaseType()==14) { //数据库
            Connection connection = getConnection(tInsDatabase.getId());
            //查询数据源
            try {
                //获取网页传递的参数
//                List<ReqKeyValue> param=new ArrayList<>();
                Map<String,Object> parameters = createAccessParams(reqAccessReport.getParam());
                //Jaspersoft转换成HTML代码的样式,
                //JasperRunManager.runReportToHtmlFile(jaserFile.getAbsolutePath(), destFileName, parameters,connection);
                //此时主子报表使用同一个数据源
                if(fileList.size()>0){
                    String SUBREPORT_DIR=fileServerConfig.getTmpPath();
                    parameters.put("SUBREPORT_DIR",SUBREPORT_DIR);
                }
                //使用JasperPrint
                JasperPrint jasperPrint= JasperFillManager.fillReport(jaserFile.getAbsolutePath(),parameters,connection);
//                JasperExportManager.exportReportToHtmlFile(jasperPrint,destFileName);
                //JRHtmlExporter exporter = new JRHtmlExporter();
//                return destFileName;
                request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,jasperPrint);
                HtmlExporter exporterHTML = new HtmlExporter();
                SimpleExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
                exporterHTML.setExporterInput(exporterInput);

                SimpleHtmlExporterOutput exporterOutput;

                exporterOutput = new SimpleHtmlExporterOutput(response.getOutputStream());
                exporterOutput.setImageHandler(new WebHtmlResourceHandler("image?image={0}"));
                exporterHTML.setExporterOutput(exporterOutput);

                SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
                reportExportConfiguration.setWhitePageBackground(false);
                reportExportConfiguration.setRemoveEmptySpaceBetweenRows(true);
                exporterHTML.setConfiguration(reportExportConfiguration);
                exporterHTML.exportReport();
            }catch (Exception e){
                logger.error("生成报表出现错误",e);
            }
        }else if(tInsDatabase.getDatabaseType()!=14){            //http接口数据源
            //获取实例对应的报表
            //解析field对应的类型
            Map<String, String> fieldMap = parseFieldType(tReportIns.getId());
            //获取报表实例对应的数据源
            Long datasourceId = tReportIns.getDatasourceId();
            tInsDatabase = tInsDatabaseService.getById(datasourceId);
            if(ObjectUtils.isEmpty(tInsDatabase)){
                return ;
            }
            String url = tInsDatabase.getUrl();
            //调用http接口获取数据
            DataSourceBeanFactory dataSourceBeanFactory=new DataSourceBeanFactory();
            ResultInfo resultInfo = dataSourceBeanFactory.getInstance0(url);
            if(ObjectUtils.isEmpty(resultInfo.getData())){
                return;
            }
            //data中的数据已经做了处理,返回的是JSONArray格式
            JSONArray jsonArray = (JSONArray) resultInfo.getData();
            if(ObjectUtils.isEmpty(jsonArray)||jsonArray.size()==0){
                return;
            }
            //对jsonArray中的数据类型做转换,和报表中的数据类型保持一致
            convert(jsonArray,fieldMap);
            //填充生成报表
            try {
                JasperPrint jasperPrint = JasperFillManager.fillReport(jaserFile.getAbsolutePath(), null, new JRBeanCollectionDataSource(jsonArray));
                return;
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new FFNullException("未找到匹配的数据源类型");
        }
    }



    /**
     * 获取实例对应的jasper文件
     * @param tInsReport
     * @return
     */
    @NotNull
    private File getJasperFile(TReportIns tInsReport) {
        try {
            //下载并编译文件
            ByteArrayOutputStream byteArrayOutputStream = downLoadAndCompileXml(tInsReport.getJasperFile());
            //返回jasper文件
            File file=new File(path+"/"+UUID.randomUUID()+".japser");
            FileOutputStream out=new FileOutputStream(file);
            byteArrayOutputStream.writeTo(out);
            out.close();
            byteArrayOutputStream.close();
            return file;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    /**
     * 获取数据源
     * @param id
     * @return
     */
    private Connection getConnection(Long id) {
        TInsDatabase tInsDatabase = tInsDatabaseService.getById(id);
        try {
            String driverClass = tInsDatabase.getDriverClassName();
            Class.forName(driverClass);
            Connection conn = DriverManager.getConnection(tInsDatabase.getUrl(),tInsDatabase.getUsername(),tInsDatabase.getPassword());
            return conn;
        }catch (Exception e){
            logger.error("创建数据库链接失败",e);
        }
        return null;
    }



    /**
     * 报表参数处理
     * @param param
     * @return
     */
    private Map<String, Object> createAccessParams(String param) {
        if(StrUtils.isNull(param)){
            return new HashMap<>();
        }
        String[] split = param.split("&");
        List<ReqKeyValue> paramList = Arrays.stream(split).map(s -> {
            ReqKeyValue reqKeyValue = new ReqKeyValue();
            String[] split1 = s.split("=");
            reqKeyValue.setKey(split1[0]);
            reqKeyValue.setValue(split1[1]);
            return reqKeyValue;
        }).collect(Collectors.toList());
        Map<String, Object> objectMap = new HashMap<>();
        paramList.stream().forEach(reqKeyValue -> {
            objectMap.put(reqKeyValue.getKey(),reqKeyValue.getValue());
        });
        return objectMap;
    }



    /**
     * 下载并编译xml文件
     * @param uri
     * @return
     */
    private ByteArrayOutputStream downLoadAndCompileXml(String uri) {
        byte [] xmlConetnt = null;
        try {
//            xmlConetnt = downloadFileNew(fileServerConfig.getServerip()+uri);
            xmlConetnt= Files.readAllBytes(Paths.get(uri));
        }catch (Exception e){
            logger.error("下载报表定义文件异常",e);
            throw new RuntimeException("下载报表定义文件异常");
        }

        if(xmlConetnt ==null || xmlConetnt.length<=0){
            throw new FFNullException("文件下载失败，文件大小为0");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            JasperCompileManager.compileReportToStream(new ByteArrayInputStream(xmlConetnt),outputStream);
        }catch (Exception e){
            logger.error("报表文件jrxml编译异常",e);
            throw new RuntimeException("报表文件编译异常");
        }

        if(outputStream.size()<=0){
            throw new FFNullException("文件编译失败，文件编译后大小为0");
        }

        //编译没有问题后，上传编译后的文件到文件服务器
        //设置文件名称
//        String defPath=tDefReport.getJasperXmlPath();
        //从路径中获取文件名
//        int index=defPath.lastIndexOf("/");
//        String subStr=defPath.substring(index);
//        String subStr1=subStr.substring(0,subStr.lastIndexOf("."));
//        String fileName =subStr1+".jasper";
//        InputStream fileInputStream = new ByteArrayInputStream(outputStream.toByteArray());
//        String path = fileServerConfig.getPathprefix() + DateUtils.getYear(LocalDateTime.now())+"/"+DateUtils.getMonth(LocalDateTime.now())+"/"
//                +DateUtils.getDay(LocalDateTime.now())+"/"+DateUtils.getHour(LocalDateTime.now())+"/"+DateUtils.getMinutes(LocalDateTime.now())+"/";
//        return exampleFileService.upload(fileName,path,fileInputStream,"originalname");
        return outputStream;
    }

    /**
     * 下载byte格式
     * @param uri
     * @return
     * @throws Exception
     */
    public byte[] downloadFileNew(String uri)throws Exception{
        OkHttpClient client = CusHttpUtil.getHttpClient();
        Request request = new Request.Builder()
                //访问路径
                .url(uri)
                .build();
        Response response = null;
        response = client.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        return bytes;
    }


    /**
     * 解析字段类型
     * @param defId
     * @return
     */
    public Map<String,String> parseFieldType(Long defId){
        TReportIns tDefReport = getById(defId);
        if(tDefReport==null){
            throw new FFNullException("无法找到对应的报表文件数据记录");
        }
        byte[] xmlContent = null;
        try {
            xmlContent =  downloadFileNew(fileServerConfig.getServerip()+tDefReport.getFileUrl());
        }catch (Exception e){
            logger.error("下载报表定义文件异常",e);
            throw new RuntimeException("下载报表定义文件异常");
        }

        if(xmlContent==null || xmlContent.length<=0){
            throw new FFNullException("报表文件内容为null");
        }
        //解析xml
        SAXReader saxReader=new SAXReader();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlContent);
        Document document = null;
        try {
            document = saxReader.read(inputStream);
        }catch (Exception e){
            logger.error("读取xml文件异常",e);
            throw new RuntimeException("读取xml文件异常");
        }
        Element rootElm  = document.getRootElement();
        Map<String,String> map=new HashMap<>();
        List<Element> elementList = rootElm.elements("field");
        if(ObjectUtils.isNotEmpty(elementList)&&elementList.size()>0){
            for (Element element:elementList) {
                String name = element.attributeValue("name");
                String aClass = element.attributeValue("class");
                map.put(name,aClass);
            }
        }
        return map;
    }


    /**
     * http数据源返回数据类型转换
     * @param jsonArray
     * @param fieldMap
     */
    public void convert(JSONArray jsonArray,Map<String,String> fieldMap){
        Set<String> fields = fieldMap.keySet();
        //判断数据量的多少
        if(jsonArray.size()>1000){
            //为了效率使用多线程进行处理(使用双重循环也可以,但是有可能数据量很多,所以在这里如果超过1000条就将任务分解)
            //将任务进行拆分,每1000条数据用一个线程进行转换
            try {
                //创建线程池,使用4个线程
                ConvertTask convertTask=new ConvertTask(0,jsonArray.size(),jsonArray,fieldMap);
                ForkJoinPool fjp=new ForkJoinPool(4);
                ForkJoinTask forkJoinTask = fjp.submit(convertTask);
                //任务执行完成
                forkJoinTask.get();
                if(forkJoinTask.isCompletedAbnormally()){
                    logger.info("多线程处理数据转换时异常:{}",forkJoinTask.getException());
                }
                fjp.shutdown();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        //数量小于1000,直接用循环处理
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject= (JSONObject) jsonArray.get(i);
            Set<String> strings1= jsonObject.keySet();
            for (String key:fields) {
                if(strings1.contains(key)) {
                    //获取typeName
                    String typeByname = fieldMap.get(key);
                    //转换类型
                    if (typeByname.equals(TypeEnum.Long.getName())) {
                        Long result = jsonObject.getLong(key);
                        jsonObject.put(key, result);
                    }else if(typeByname.equals("java.lang.Double")){
                        Double aDouble = jsonObject.getDouble(key);
                        jsonObject.put(key,aDouble);
                    }
                }
            }
        }
    }


    /**
     * 带返回值的任务
     */
    class ConvertTask extends RecursiveTask {
        static final int SEQUENTIAL_THRESHOLD = 1000;

        int low=0;
        int high=0;
        JSONArray jsonArray;

        Map<String,String> fieldMap;

        public ConvertTask(int low, int high, JSONArray jsonArray, Map<String, String> fieldMap) {
            this.low = low;
            this.high = high;
            this.jsonArray = jsonArray;
            this.fieldMap = fieldMap;
        }

        @Override
        protected Object compute() {
            if(high-low<=SEQUENTIAL_THRESHOLD){
                for (int i = low; i < high; i++) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                    Set<String> strings = jsonObject.keySet();
                    Set<String> fields = fieldMap.keySet();
                    for (String key:strings) {
                        if(fields.contains(key)){
                            //获取typeName
                            String typeByname = fieldMap.get(key);
                            //转换处理
                            if (typeByname.equals(TypeEnum.Long.getName())) {
                                Long result = jsonObject.getLong(key);
                                jsonObject.put(key, result);
                            }
                        }
                    }
                }
                return null;
            }else{
                //任务拆分
                int mid=low+(high-low)/2;
                ConvertTask left=new ConvertTask(low,mid,jsonArray,fieldMap);
                ConvertTask right=new ConvertTask(mid,high,jsonArray,fieldMap);
                left.join();
                right.join();
            }
            return null;
        }

    }


    /**
     * 导出报表
     * @param reqAccessReport
     * @param request
     * @param response
     */
    @Override
    public void accessReportHtml(ReqAccessReport reqAccessReport, HttpServletRequest request, HttpServletResponse response) {
        JasperPrint jasperPrint = getJasperPrint(reqAccessReport);
        if(com.example.base.common.utils.str.StrUtils.isNull(reqAccessReport.getExportType())){
            reqAccessReport.setExportType("html");
        }
        //根据不同类型，导出各式报表
        if ("pdf".equals(reqAccessReport.getExportType())) {
            try {
                String fileName = new String((UUID.randomUUID()+".pdf").toString().getBytes(),"ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名（这个信息头会告诉浏览器这个文件的名字和类型）
            }catch (Exception e){
                logger.error("创建导出名称失败");
            }
            JRPdfExporter exporter = new JRPdfExporter();
            try {
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                exporter.exportReport();
                JasperExportManager.exportReportToPdfStream(jasperPrint,response.getOutputStream());
            } catch (IOException e) {
                logger.error("IOException occured", e);
                e.printStackTrace();
            } catch (JRException e) {
                logger.error("JRException while exporting for pdf format", e);
                e.printStackTrace();
            }
        } else if ("xls".equals(reqAccessReport.getExportType())) {
            JRXlsExporter exporter = new JRXlsExporter();
            try {
                String fileName = new String((UUID.randomUUID()+".xlsx").toString().getBytes(),"ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名（这个信息头会告诉浏览器这个文件的名字和类型）
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
//                configuration.setOnePagePerSheet(true);
                configuration.setOnePagePerSheet(false);
                exporter.setConfiguration(configuration);
                exporter.exportReport();
            } catch (JRException e) {
                logger.error("JRException while exporting for xls format", e);
                e.printStackTrace();
            } catch (IOException e) {
                logger.error("IOException occured", e);
                e.printStackTrace();
            }
        } else if ("csv".equals(reqAccessReport.getExportType())) {
            JRCsvExporter exporter = new JRCsvExporter();
            try {
                String fileName = new String((UUID.randomUUID()+".csv").toString().getBytes(),"ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名（这个信息头会告诉浏览器这个文件的名字和类型）
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleWriterExporterOutput(response.getOutputStream()));
                exporter.exportReport();
            } catch (IOException e) {
                logger.error("IOException occured", e);
                e.printStackTrace();
            } catch (JRException e) {
                logger.error("JRException while exporting report csv format", e);
                e.printStackTrace();
            }
        } else if ("html".equals(reqAccessReport.getExportType())) {
            request.getSession().setAttribute(ImageServlet.DEFAULT_JASPER_PRINT_SESSION_ATTRIBUTE,jasperPrint);
            HtmlExporter exporterHTML = new HtmlExporter();
            SimpleExporterInput exporterInput = new SimpleExporterInput(jasperPrint);
            exporterHTML.setExporterInput(exporterInput);

            SimpleHtmlExporterOutput exporterOutput;
            try {
                exporterOutput = new SimpleHtmlExporterOutput(response.getOutputStream());
                exporterOutput.setImageHandler(new WebHtmlResourceHandler("image?image={0}"));
                exporterHTML.setExporterOutput(exporterOutput);

                SimpleHtmlReportConfiguration reportExportConfiguration = new SimpleHtmlReportConfiguration();
                reportExportConfiguration.setWhitePageBackground(false);
                reportExportConfiguration.setRemoveEmptySpaceBetweenRows(true);
                exporterHTML.setConfiguration(reportExportConfiguration);
                exporterHTML.exportReport();
            } catch (IOException e) {
                logger.error("IOException occured", e);
                e.printStackTrace();
            } catch (JRException e) {
                logger.error("JRException while exporting for html format", e);
                e.printStackTrace();
            }
        } else if ("docx".equals(reqAccessReport.getExportType())) {
            try {
                String fileName = new String((UUID.randomUUID()+".docx").toString().getBytes(),"ISO8859-1");
                response.setHeader("Content-Disposition", "attachment;fileName=" + fileName);// 设置文件名（这个信息头会告诉浏览器这个文件的名字和类型）
            }catch (Exception e){
                logger.error("创建导出名称失败");
            }
            JRDocxExporter exporter = new JRDocxExporter();
            try {
                exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(response.getOutputStream()));
                exporter.exportReport();
            } catch (IOException e) {
                logger.error("IOException occured", e);
                e.printStackTrace();
            } catch (JRException e) {
                logger.error("JRException while exporting for docx format", e);
                e.printStackTrace();
            }
        }
    }



    /**
     * 获取JasperPrint
     * @param reqAccessReport
     * @return
     */
    public JasperPrint getJasperPrint(ReqAccessReport reqAccessReport){
        //先找到报表实例
        LambdaQueryWrapper<TReportIns> tInsReportQueryWrapper = new LambdaQueryWrapper<>();
        tInsReportQueryWrapper.eq(TReportIns::getId,reqAccessReport.getId());
        //获取主报表实例
        List<TReportIns> tInsReportList = list(tInsReportQueryWrapper);
        if(tInsReportList ==null || tInsReportList.size()==0){
            throw new FFNullException("未找到要访问的报表");
        }
        if(tInsReportList.size()>1){
            throw new FFNullException("系统异常，出现1个以上相同报表实例");
        }
        //报表实例
        TReportIns tReportIns = tInsReportList.get(0);
        //如果临时文件中没有当前jasper文件，那么将重新下载.文件的命名规则为：key+"_"+id+".jasper"
        File jaserFile = getJasperFile(tReportIns);
        //获取子报表实例
        tInsReportQueryWrapper=new LambdaQueryWrapper<>();
        tInsReportQueryWrapper.eq(TReportIns::getPid,tReportIns.getId());
        List<TReportIns> subInsReportList=list(tInsReportQueryWrapper);
        List<File> fileList=new ArrayList<>();
        //子报表文件
        for(TReportIns insReport:subInsReportList){
            File subjapserFile=getJasperFile(insReport);
            fileList.add(subjapserFile);
        }
        //获取报表的数据源
        TInsDatabase tInsDatabase = tInsDatabaseService.getById(tReportIns.getDatasourceId());
        if(ObjectUtils.isEmpty(tInsDatabase)){
            //填充生成报表
            try {
                JasperPrint jasperPrint = JasperFillManager.fillReport(jaserFile.getAbsolutePath(), null, new JRBeanCollectionDataSource(null));
                return jasperPrint;
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
        }
        //不同的数据源类型，有不同的处理方式
        if(tInsDatabase.getDatabaseType()==14) { //数据库
            Connection connection = getConnection(tInsDatabase.getId());
            //查询数据源
            try {
                //获取网页传递的参数
                Map<String,Object> parameters = createAccessParams(reqAccessReport.getParam());
                //此时主子报表使用同一个数据源
                if(fileList.size()>0){
                    String SUBREPORT_DIR=fileServerConfig.getTmpPath();
                    parameters.put("SUBREPORT_DIR",SUBREPORT_DIR);
                }
                //使用JasperPrint
                JasperPrint jasperPrint=JasperFillManager.fillReport(jaserFile.getAbsolutePath(),parameters,connection);
                return jasperPrint;
            }catch (Exception e){
                logger.error("生成报表出现错误",e);
            }
        }else if(tInsDatabase.getDatabaseType()!=14){
            //说明不是数据库数据源,使用自定义的数据源
            //获取实例id
            //http接口数据源
            //获取实例对应的报表
            //解析field对应的类型
            Map<String, String> fieldMap = parseFieldType(tReportIns.getId());
            //获取报表实例对应的数据源
            Long datasourceId = tReportIns.getDatasourceId();
            TInsDatabase tInsDatabase1 = tInsDatabaseService.getById(datasourceId);
            if(ObjectUtils.isEmpty(tInsDatabase1)){
                return null;
            }
            String url = tInsDatabase1.getUrl();
            //调用http接口获取数据
            DataSourceBeanFactory dataSourceBeanFactory=new DataSourceBeanFactory();
            ResultInfo resultInfo = dataSourceBeanFactory.getInstance0(url);
            if(ObjectUtils.isEmpty(resultInfo.getData())){
                return null;
            }
            //data中的数据已经做了处理,返回的是JSONArray格式
            JSONArray jsonArray = (JSONArray) resultInfo.getData();
            if(ObjectUtils.isEmpty(jsonArray)||jsonArray.size()==0){
                return null;
            }
            //对jsonArray中的数据类型做转换,和报表中的数据类型保持一致
            convert(jsonArray,fieldMap);
            //填充生成报表
            try {
                JasperPrint jasperPrint = JasperFillManager.fillReport(jaserFile.getAbsolutePath(), null, new JRBeanCollectionDataSource(jsonArray));
                return jasperPrint;
            } catch (JRException e) {
                throw new RuntimeException(e);
            }
        }else{
            throw new FFNullException("未找到匹配的数据源类型");
        }
        return null;
    }


}
