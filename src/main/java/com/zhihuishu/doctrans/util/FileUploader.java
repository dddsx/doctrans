package com.zhihuishu.doctrans.util;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.zhihuishu.doctrans.util.img.ImgConverter.SYMBOL_POINT;

public class FileUploader {
    
    private final static Logger logger = LoggerFactory.getLogger(FileUploader.class);
    
    private final static String threadNamePrefix = "doctrans-fileupload-";
    
    private final static AtomicInteger threadNum = new AtomicInteger(1);
    
    /** 创建固定大小为20的线程池，用来并发上传文件 */
    private final static ExecutorService uploadExecutor = new ThreadPoolExecutor(
            20,
            20,
            0,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            r -> {
                Thread t = new Thread(Thread.currentThread().getThreadGroup(), r,
                        threadNamePrefix + threadNum.getAndIncrement());
                if (t.getPriority() != Thread.NORM_PRIORITY) {
                    t.setPriority(Thread.NORM_PRIORITY);
                }
                return t;
            }
    );
    
    private final static String UPLOAD_SUCCESS_CODE = "0";
    
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
            List<Future<Map<String, String>>> futures = new ArrayList<>();
            
            // 开启上传任务
            for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
                futures.add(
                    uploadExecutor.submit(
                        () -> {
                            String imageName = entry.getKey();
                            byte[] bytes = entry.getValue();
                            // 将图片上传到OSS服务器, 并获得URL
                            String url = uploadToOSS(new ByteArrayInputStream(bytes), getRandomFilename(imageName, format));
                            return Collections.singletonMap(imageName, url);
                        }
                    )
                );
            }
            
            // 阻塞获取上传结果
            for (Future<Map<String, String>> future : futures) {
                try {
                    Map<String, String> singleMap = future.get();
                    imageUrls.putAll(singleMap);
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("http请求异常", e);
                    System.out.println("ftp上传出现异常");
                    e.printStackTrace();
                    break;
                }
            }
        } else {
            for (Map.Entry<String, byte[]> entry : imageBytes.entrySet()) {
                String imageName = entry.getKey();
                byte[] bytes = entry.getValue();
                try {
                    // 将图片上传到OSS服务器, 并获得URL
                    String url = uploadToOSS(new ByteArrayInputStream(bytes), getRandomFilename(imageName, format));
                    imageUrls.put(entry.getKey(), url);
                } catch (Exception e) {
                    logger.error("http请求异常", e);
                    break;
                }
            }
        }
        return imageUrls;
    }
    
    private static String uploadToOSS(InputStream inputStream, String fileName) {
        String responseData = OSSPublicUploadInterface.ftpAttachment(inputStream, "doctrans",
                "docx2html", fileName);
        return getOssUrl(responseData);
    }
    
    private static String getOssUrl(String responseData) {
        JSONObject jsonObject = JSONObject.parseObject(responseData);
        if ( !UPLOAD_SUCCESS_CODE.equals(jsonObject.getString("code")) ) {
            throw new IllegalStateException("http请求异常");
        }
        JSONObject data = jsonObject.getJSONObject("data");
        return data.getString("path");
    }
    
    /**
     * 生成带格式后缀的UUID文件名
     */
    private static String getRandomFilename(String originName, String format) {
        String uuid = UUIDUtils.createUUID();
        if (format != null) {
            return uuid + SYMBOL_POINT + format;
        }
        
        if (originName.contains(SYMBOL_POINT)) {
            format = originName.substring(originName.lastIndexOf(SYMBOL_POINT) - 1);
            return uuid + SYMBOL_POINT + format;
        }
        return uuid;
    }
}
