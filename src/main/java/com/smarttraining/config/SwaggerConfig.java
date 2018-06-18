package com.smarttraining.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.google.common.collect.Sets;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig { //extends WebMvcConfigurationSupport{

	
	@Value("${app.host:localhost}")
	private String apiHost;
	
    @Value("${app.api-doc:/api-docs}")
    private String swagger2Endpoint;
	
    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
        		.protocols(Sets.newHashSet("http"))
//        		.host(apiHost)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.smarttraining.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }
    
    private ApiInfo metaData() {
        return new ApiInfo(
                "Smart Training API",
                "Smart Training API",
                "0.0.1",
                "Terms of service",
                new Contact("Aaron LUO", "", "kinglz2003@hotmail.com"),
                "Apache License Version 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0"
                );
    }
}
