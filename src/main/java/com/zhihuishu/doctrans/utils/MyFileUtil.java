package com.zhihuishu.doctrans.utils;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MyFileUtil {

    public static File downloadFile(String urlPath, String downloadDir) {
        File file = null;
        OutputStream out = null;
        BufferedInputStream bin = null;
        try {
            // 统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;
            // 设定请求的方法，默认是GET
            httpURLConnection.setRequestMethod("POST");
            // 设置字符编码
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            // 打开到此 URL 引用的资源的通信链接（如果尚未建立这样的连接）。
            httpURLConnection.connect();

            // 文件大小
            int fileLength = httpURLConnection.getContentLength();

            // 文件名
            String filePathUrl = httpURLConnection.getURL().getFile();
            String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);

            System.out.println("file length---->" + fileLength);

            bin = new BufferedInputStream(httpURLConnection.getInputStream());

            String path = downloadDir + File.separatorChar + fileFullName;
            file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            out = new FileOutputStream(file);
            int size = 0;
            int len = 0;
            byte[] buf = new byte[1024];
            while ((size = bin.read(buf)) != -1) {
                len += size;
                out.write(buf, 0, size);
                // 打印下载百分比
                // System.out.println("下载了-------> " + len * 100 / fileLength +
                // "%\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(bin != null) {
                try {
                    bin.close();
                } catch (IOException ignored) {

                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException ignored) {

                }
            }
        }
        return file;
    }

    public static String uploadFileToOSS(File file){
        String ossUrl = null;
        try {
            ossUrl = OSSPublicUploadInterface.ftpAttachment(file, "doctrans", "docx2html");
            if (!StringUtils.isEmpty(ossUrl)) {
                JSONObject jsonObject = JSONObject.parseObject(ossUrl);
                JSONObject data = jsonObject.getJSONObject("data");
                if (data != null) {
                    ossUrl = data.getString("path");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ossUrl;
    }
}
