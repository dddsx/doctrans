package com.zhihuishu.doctrans.http;

import com.zhihuishu.doctrans.model.WmfData;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
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

import static org.apache.commons.io.IOUtils.EOF;

public class WMFConvertRequest {
    
    private final static String host = "http://192.168.40.231:5111/wmf/imageHandler.ashx?action=ConvertToPngBatch";
    
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    
    public Map<String, byte[]> uploadAndConvert(Map<String, WmfData> wmfDatas) throws IOException {
        byte[] zipData = null;
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(host);
        
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(Integer.MAX_VALUE)
                    .setSocketTimeout(Integer.MAX_VALUE)
                    .build();
            httpPost.setConfig(requestConfig);
        
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (Map.Entry<String, WmfData> entry : wmfDatas.entrySet()) {
                WmfData wmfData = entry.getValue();
                String filenameAndParam = getFilenameAndParam(wmfData.getPlaceholder(),
                        wmfData.getWidth().intValue(), wmfData.getHeight().intValue());
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
    
    private Map<String, byte[]> extractPNGDatasInZip(byte[] zipData) {
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
                        pngDatas.put(zipEntry.getName(), output.toByteArray());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pngDatas;
    }
    
    private static String getFilenameAndParam(String filename, Integer width, Integer height) {
        StringBuilder sb = new StringBuilder("n_" + filename);
        if (width != null) {
            sb.append(",w_").append(width);
        }
        if (height != null) {
            sb.append(",h_").append(height);
        }
        return sb.toString();
    }
}
