package com.example.core.report.generate;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.commons.lang3.StringUtils;

import java.util.Scanner;

public class MyBatisCodeGenerator {
    /**
     * <p>
     * 读取控制台内容
     * </p>
     */
    public static String scanner(String tip) {
        System.out.println(tip);
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.isNotEmpty(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }

    public static void main(String[] args) {
        // 代码生成器
        AutoGenerator mpg = new AutoGenerator();

        //String projectPath = System.getProperty("user.dir"); // 当前项目目录
        String projectPath = "/Users/liulei/project";
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        gc.setAuthor("liulei").setOutputDir(projectPath + "/src/main/java")
                .setFileOverride(true)//覆盖现有文件
                .setServiceName("%sService")//.setSwagger2(true)
                .setOpen(false).setBaseResultMap(true);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        //String dbUrl = "jdbc:mysql://192.168.0.6:3306/ffdevops?characterEncoding=UTF-8&useUnicode=true&useSSL=false";
        String dbUrl ="jdbc:mysql://192.168.0.37:3306/exampleReport?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=UTC";
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDbType(DbType.MYSQL).setUrl(dbUrl)
                .setUsername("example").setPassword("example")
                .setDriverName("com.mysql.cj.jdbc.Driver");
        mpg.setDataSource(dataSourceConfig);

        // 包配置
        PackageConfig pc = new PackageConfig();
        // pc.setModuleName(scanner("模块名"));
        pc.setParent(null)
                .setXml("mapper")
                .setParent("com.example.core.report");
        mpg.setPackageInfo(pc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setInclude(scanner("表名:"));//
        strategy.setRestControllerStyle(true);
        //strategy.setSuperControllerClass("com.example.base.web.controller.BaseController");
        strategy.setControllerMappingHyphenStyle(true);
        strategy.setEntityTableFieldAnnotationEnable(true);
        strategy.setTablePrefix(pc.getModuleName() + "_");
        mpg.setStrategy(strategy);
        mpg.execute();
    }
}
