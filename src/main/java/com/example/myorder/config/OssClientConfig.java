package com.example.myorder.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OssClientConfig {
    private final OssConfig ossConfig;

    public OssClientConfig(OssConfig ossConfig) {
        this.ossConfig = ossConfig;
    }

    @Bean
    public OSS ossClient() {
        return new OSSClientBuilder().build(
                ossConfig.getEndpoint(),
                ossConfig.getAccessKeyId(),
                ossConfig.getAccessKeySecret());
    }
}