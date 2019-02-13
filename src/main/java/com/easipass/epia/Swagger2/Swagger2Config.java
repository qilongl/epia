package com.easipass.epia.Swagger2;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by lql on 2018/9/25 10:11
 **/
@Configuration //标记配置类
@EnableSwagger2 //开启在线接口文档
public class Swagger2Config {
    /**
     * 添加摘要信息(Docket)
     */
    @Bean
    public Docket controllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("EPIA接口文档")
                        .description("EPIA解析组件接口文档说明")
                        .contact(new Contact("lql", null, "qilong0226@gmail.com"))
                        .version("版本号:1.0")
                        .build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.easipass.epia"))
                .paths(PathSelectors.any())
                .build();
    }
}
