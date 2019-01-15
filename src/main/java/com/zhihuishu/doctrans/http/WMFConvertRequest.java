package com.zhihuishu.doctrans.http;

import com.zhihuishu.doctrans.model.WmfData;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.zhihuishu.doctrans.util.img.ImgConverter.EXT_PNG;
import static org.apache.commons.io.IOUtils.EOF;

public class WMFConvertRequest {
    
    private final static String host = "http://47.97.45.244/ImageConvert/imageHandler.ashx?action=ConvertToPngBatch";
    
    /** 抓包代理软件host */
    // private final static HttpHost proxy = new HttpHost("localhost", 8888);
    
    private final static int DEFAULT_BUFFER_SIZE = 4096;
    
    /** 转换的png放大倍数 */
    private final static int PNG_MUTIPLE_SIZE = 4;
    
    /** 预计http连接耗时5s */
    private final static int HTTP_CONNECT_USE_TIME = 5000;
    
    /** 预计每张图片转换耗时60ms */
    private final static int EVERY_PICTURE_USE_TIME = 60;
    
    /**
     * 调用C#服务请求转换wmf图片为png
     * @param wmfDatas wmf元数据
     * @return png数据
     * @throws IOException 服务调用异常
     */
    public static Map<String, byte[]> uploadAndConvert(Map<String, WmfData> wmfDatas) throws IOException {
        byte[] zipData;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(host);
        
            int pictureNum = wmfDatas.size();
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(HTTP_CONNECT_USE_TIME + pictureNum * EVERY_PICTURE_USE_TIME)
                    .setSocketTimeout(HTTP_CONNECT_USE_TIME + pictureNum * EVERY_PICTURE_USE_TIME)
                    //.setProxy(proxy)
                    .build();
            httpPost.setConfig(requestConfig);
        
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, WmfData> entry : wmfDatas.entrySet()) {
                WmfData wmfData = entry.getValue();
                Double width = wmfData.getWidth();
                Double height = wmfData.getHeight();
                String filenameAndParam = getFilenameAndParam(wmfData.getPlaceholder(),
                        width == null ? null : width.intValue(),
                        height == null ? null : height.intValue()
                );
                byte[] datas = wmfData.getBytes();
                multipartEntityBuilder.addBinaryBody(filenameAndParam, datas, ContentType.create("image/wmf"), filenameAndParam);
            }
            httpPost.setEntity(multipartEntityBuilder.build());
            
            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                InputStream inputStream = new BufferedInputStream(response.getEntity().getContent());
                zipData = IOUtils.toByteArray(inputStream);
            }
        }
        return extractPNGDatasInZip(zipData);
    }
    
    private static Map<String, byte[]> extractPNGDatasInZip(byte[] zipData) throws IOException {
        if (zipData == null) {
            throw new IllegalArgumentException();
        }
        
        Map<String, byte[]> pngDatas = new HashMap<>();
        try (ZipInputStream input = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry zipEntry;
            while ((zipEntry = input.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
                        int n;
                        while (EOF != (n = input.read(buffer, 0, buffer.length))) {
                            output.write(buffer, 0, n);
                        }
                        String filename = zipEntry.getName();
                        String placeholder = filename;
                        if (StringUtils.endsWithIgnoreCase(filename, EXT_PNG)) {
                            placeholder = filename.substring(0, filename.length() - 4);
                        }
                        pngDatas.put(placeholder, output.toByteArray());
                    }
                }
            }
        }
        return pngDatas;
    }
    
    private static String getFilenameAndParam(String filename, Integer width, Integer height) {
        StringBuilder sb = new StringBuilder("n_" + filename);
        
        if (width != null) {
            sb.append(",w_").append(width * PNG_MUTIPLE_SIZE);
        }
        
        if (height != null) {
            sb.append(",h_").append(height * PNG_MUTIPLE_SIZE);
        }
        
        return sb.toString();
    }
}
