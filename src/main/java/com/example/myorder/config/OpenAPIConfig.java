package com.example.myorder.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Value("${myorder.openapi.dev-url}")
    private String devUrl;

    @Bean
    public OpenAPI myOpenAPI() {
        Server devServer = new Server();
        devServer.setUrl(devUrl);
        devServer.setDescription("开发环境");

        Contact contact = new Contact();
        contact.setName("点餐系统API");
        contact.setEmail("support@example.com");
        contact.setUrl("https://www.example.com");

        Info info = new Info()
                .title("点餐系统 API")
                .version("1.0")
                .contact(contact)
                .description("这是一个点餐系统的API文档，包含用户、菜品、购物车、订单等接口。");

        return new OpenAPI()
                .info(info)
                .servers(List.of(devServer));
    }
} 