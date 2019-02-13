package com.easipass.epia;

import com.easipass.epia.beans.SysProperties;
import com.easipass.epia.service.XmlBusiConfigContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class EpiaApplication {
    private static Logger logger = LoggerFactory.getLogger(EpiaApplication.class);
    public static ApplicationContext ctx;

    @Bean
    @LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static ApplicationContext start() throws Exception {
        ctx = SpringApplication.run(EpiaApplication.class);
        //1、加载配置文件
        SysProperties.init();
        //2、初始化业务组件
        XmlBusiConfigContainer xmlBusiConfigContainer = (XmlBusiConfigContainer) ctx.getBean("xmlBusiConfigContainer");
        xmlBusiConfigContainer.init(ctx);

        logger.info("============================EpiaApplication 启动完成===================================");
        return ctx;
    }


    public static void main(String[] args) throws Exception {
        start();
    }

}

