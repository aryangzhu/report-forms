package com.fivefu.core.report;

import com.fivefu.module.dictionary.config.EnableDict;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

@SpringBootApplication
@EnableDict
public class ReportServerApplication {

        private final static Logger logger= LoggerFactory.getLogger(ReportServerApplication.class);
        public static void main(String[] args) throws UnknownHostException {
            System.setProperty("javax.xml.parsers.DocumentBuilderFactory","com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            ConfigurableApplicationContext application = SpringApplication.run(ReportServerApplication.class,args);
            Environment env = application.getEnvironment();
            logger.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running! Access URLs:\n\t" +
                            "Index: \thttp://{}:{}{}/\n\t" +
                            "Doc: \thttp://{}:{}{}/doc.html\n" +
                            "----------------------------------------------------------",
                    env.getProperty("server.servlet.context-path"),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"),
                    InetAddress.getLocalHost().getHostAddress(),
                    env.getProperty("server.port"),
                    env.getProperty("server.servlet.context-path"));
        }

}
