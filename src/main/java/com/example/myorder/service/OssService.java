package com.example.myorder.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.example.myorder.config.OssConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OssService {
    // 注入 OSS 客户端和配置参数（通过 Spring Boot 自动注入）
    private final OSS ossClient;
    private final OssConfig ossConfig;

    /**
     * 新增/上传文件（如果同一路径下的文件已存在则覆盖）
     * 
     * @param fileName    上传文件在 OSS 中的路径和名称，例如 "images/abc.jpg"
     * @param inputStream 文件的输入流
     * @return 上传后文件的完整访问 URL
     */
    public String uploadFile(String fileName, InputStream inputStream) {
        fileName = addImagesPrefix(fileName);
        PutObjectRequest putObjectRequest = new PutObjectRequest(
                ossConfig.getBucketName(), fileName, inputStream);
        System.out.println("Uploading file to OSS: " + fileName);
        ossClient.putObject(putObjectRequest);
        return getFileUrl(fileName);
    }

    /**
     * 获取文件访问 URL（读取操作）
     * 
     * @param fileName OSS 中存储的文件路径及名称
     * @return 文件访问 URL
     */
    public String getFileUrl(String fileName) {
        fileName = addImagesPrefix(fileName);
        return "https://" + ossConfig.getBucketName() + "."
                + extractEndpointHost(ossConfig.getEndpoint())
                + "/" + fileName ;
    }

    /**
     * 更新文件（覆盖上传操作）
     * 调用方式与 uploadFile 一致，OSS 中上传同一路径文件则会覆盖原文件
     * 
     * @param fileName    文件在 OSS 中的路径和名称
     * @param inputStream 新文件数据流
     * @return 更新后文件的访问 URL
     */
    public String updateFile(String fileName, InputStream inputStream) {
        fileName = addImagesPrefix(fileName);
        return uploadFile(fileName, inputStream);
    }

    /**
     * 删除文件
     * 
     * @param fileName OSS 中存储的文件路径及名称
     */
    public void deleteFile(String fileName) {
        fileName = addImagesPrefix(fileName);
        ossClient.deleteObject(ossConfig.getBucketName(), fileName);
    }

    /**
     * 列出指定前缀下的所有文件（查询操作）
     * 
     * @param prefix 文件前缀，例如 "images/"
     * @return 文件完整访问 URL 列表
     */
    public List<String> listFiles(String prefix) {
        ObjectListing objectListing = ossClient.listObjects(ossConfig.getBucketName(), prefix);
        return objectListing.getObjectSummaries().stream()
                .map(OSSObjectSummary::getKey)
                .map(this::getFileUrl)
                .collect(Collectors.toList());
    }

    // 提取 endpoint 中的主机部分（例如 "oss-cn-region.aliyuncs.com"）
    private String extractEndpointHost(String endpoint) {
        if (endpoint.startsWith("https://")) {
            return endpoint.substring("https://".length());
        } else if (endpoint.startsWith("http://")) {
            return endpoint.substring("http://".length());
        }
        return endpoint;
    }

    // 新增方法：将文件名称加上 "images/" 前缀（如果尚未加上）
    private String addImagesPrefix(String fileName) {
        if (!fileName.startsWith("images/")) {
            return "images/" + fileName;
        }
        return fileName;
    }
}