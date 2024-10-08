package com.example.core.report.controller;

import com.example.base.fileupload.constant.FileTypeEnum;
import com.example.base.fileupload.minio.services.MinioServices;
import com.example.base.web.vo.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;


@Api(tags="文件上传下载服务")
@RestController
@RequestMapping("/t-file")
public class FileUpLoadController extends BaseController{
    private final static Logger logger= LoggerFactory.getLogger(FileUpLoadController.class);

//    @Autowired
//    MinioServices minioService;


//    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
//    @ApiOperation(value = "单文件上传", httpMethod = "POST", response = ResultInfo.class, notes = "单文件上传")
//    public ResultInfo uploadfile(@RequestPart(value = "file", required = true) MultipartFile file) {
//        try {
//            String type = file.getContentType();
//            Set<String> typeSet = FileTypeEnum.getFileTypeSet();
//            if (typeSet.contains(type)) {
//                return ResultInfo.renderSuccess(minioService.uploadFile(file));
//            } else {
//                return ResultInfo.renderError("不支持该类型的文件");
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return ResultInfo.renderError("文件上传错误");
//        }
//    }

}
