package com.example.myorder.controller;

import com.example.myorder.service.OssService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
@Tag(name = "OSS文件管理", description = "阿里云OSS文件管理操作接口")
public class OssController {

    private final OssService ossService;

    /**
     * 上传文件接口
     * POST /api/oss/upload
     *
     * @param file 上传的文件（multipart/form-data格式），文件名称将自动加上指定前缀（如images/）
     * @return 上传后文件的访问 URL
     */
    @Operation(summary = "上传文件", description = "将文件上传到OSS，并返回文件访问 URL。")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(
            @Parameter(description = "上传的文件", required = true) @RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Uploading file to OSS: " + file.getOriginalFilename());
            String fileName = file.getOriginalFilename();
            String url = ossService.uploadFile(fileName, file.getInputStream());
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取文件访问 URL 接口
     * GET /api/oss/file?fileName=abc.jpg
     *
     * @param fileName 文件名称（例如："abc.jpg"）
     * @return 文件访问 URL
     */
    @Operation(summary = "获取文件URL", description = "根据文件名称获取OSS中存储文件的访问 URL。")
    @GetMapping("/file")
    public ResponseEntity<String> getFile(
            @Parameter(description = "文件名称（如\"abc.jpg\"）", required = true) @RequestParam("fileName") String fileName) {
        String url = ossService.getFileUrl(fileName);
        return ResponseEntity.ok(url);
    }

    /**
     * 更新文件接口（覆盖上传）
     * PUT /api/oss/file
     *
     * @param file 更新的文件（multipart/form-data格式），文件名称将自动加上指定前缀
     * @return 更新后文件的访问 URL
     */
    @Operation(summary = "更新文件", description = "覆盖上传新文件，更新OSS中同一路径文件并返回新的访问 URL。")
    @PutMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateFile(
            @Parameter(description = "更新的文件", required = true) @RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String url = ossService.updateFile(fileName, file.getInputStream());
            return ResponseEntity.ok(url);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除文件接口
     * DELETE /api/oss/file?fileName=abc.jpg
     *
     * @param fileName 文件名称（例如："abc.jpg"）
     * @return 状态信息
     */
    @Operation(summary = "删除文件", description = "根据文件名称删除OSS中的文件。")
    @DeleteMapping("/file")
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "文件名称（如\"abc.jpg\"）", required = true) @RequestParam("fileName") String fileName) {
        try {
            ossService.deleteFile(fileName);
            return ResponseEntity.ok("删除成功");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("删除失败：" + e.getMessage());
        }
    }

    /**
     * 列出指定前缀下的所有文件接口
     * GET /api/oss/list?prefix=images/
     *
     * @param prefix 文件前缀，例如 "images/"
     * @return 文件访问 URL 列表
     */
    @Operation(summary = "列出文件", description = "根据文件前缀列出所有文件，返回文件的访问 URL 列表。")
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles(
            @Parameter(description = "文件前缀，例如 \"images/\"", required = true) @RequestParam("prefix") String prefix) {
        List<String> urls = ossService.listFiles(prefix);
        return ResponseEntity.ok(urls);
    }
}