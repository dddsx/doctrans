package com.zhihuishu.doctrans.util;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.zhihuishu.doctrans.util.ImgConverter.SYMBOL_BOT;

public class FileUploader {
    
    private final static Logger logger = LoggerFactory.getLogger(FileUploader.class);
    
    private final static String SUCCESS_CODE = "0";
    
    /**
     * 使用多线程方式上传图片到OSS服务器
     * @param imageBytes 图片标识 map to 图片数据
     * @param format 图片格式
     * @param concurrentMode 是否多线程上传
     * @return 文件名 map to URL
     */
    public static Map<String, String> uploadImageToOSS(Map<String, byte[]> imageBytes, String format,
                                                       boolean concurrentMode) {
        Map<String, String> imageUrls = new HashMap<>();
        
        if (concurrentMode) {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            List<Future<Map<String, String>>> futures = new ArrayList<>();
            boolean hasShutdown = false;
            
            // 开启上传任务
            for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
                futures.add(executorService.submit(() -> {
                    String imageName = entry.getKey();
                    byte[] bytes = entry.getValue();
                    // 将图片上传到OSS服务器, 并获得URL
                    String url = uploadFileToOSS(new ByteArrayInputStream(bytes), getRandomFilename(imageName, format));
                    return Collections.singletonMap(imageName, url);
                }));
            }
            
            // 阻塞获取结果
            for (Future<Map<String, String>> future : futures) {
                try {
                    Map<String, String> singleMap = future.get();
                    imageUrls.putAll(singleMap);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("http请求异常", e);
                    executorService.shutdownNow();
                    hasShutdown = true;
                    break;
                }
            }
            
            if (!hasShutdown) {
                executorService.shutdown();
            }
            
        } else {
            for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
                String imageName = entry.getKey();
                byte[] bytes = entry.getValue();
                try {
                    // 将图片上传到OSS服务器, 并获得URL
                    String url = uploadFileToOSS(new ByteArrayInputStream(bytes), getRandomFilename(imageName, format));
                    imageUrls.put(entry.getKey(), url);
                } catch (Exception e) {
                    logger.error("http请求异常", e);
                    break;
                }
            }
        }
        return imageUrls;
    }

    public static String uploadFileToOSS(File file) {
        String responseData = OSSPublicUploadInterface.ftpAttachment(file, "doctrans", "docx2html");
        return getOssUrl(responseData);
    }
    
    private static String uploadFileToOSS(InputStream inputStream, String fileName) {
        String responseData = OSSPublicUploadInterface.ftpAttachment(inputStream, "doctrans",
                "docx2html", fileName);
        return getOssUrl(responseData);
    }
    
    private static String getOssUrl(String responseData) {
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        if ( !SUCCESS_CODE.equals(jsonObject.getString("code")) ) {
            throw new IllegalStateException("http请求异常");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        return data.getString("path");
    }
    
    private static String getRandomFilename(String originName, String format) {
        String uuid = UUIDUtils.createUUID();
        if (format != null) {
            return uuid + SYMBOL_BOT + format;
        }
        
        if (originName.contains(SYMBOL_BOT)) {
            String uploadFormat = originName.substring(originName.lastIndexOf(SYMBOL_BOT));
            return uuid + SYMBOL_BOT + uploadFormat;
        }
        return uuid;
    }
}
