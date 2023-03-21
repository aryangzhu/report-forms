package com.fivefu.core.report.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "fileserver")
public class FileServerConfig {

    //文件服务器IP
    private String serverip;
    private String pathprefix;
    private String tmpPath;//临时文件存储目录

    public String getPathprefix() {
        return pathprefix;
    }

    public void setPathprefix(String pathprefix) {
        this.pathprefix = pathprefix;
    }

    public String getServerip() {
        return serverip;
    }

    public void setServerip(String serverip) {
        this.serverip = serverip;
    }

    public String getTmpPath() {
        return tmpPath;
    }

    public void setTmpPath(String tmpPath) {
        this.tmpPath = tmpPath;
    }
}
