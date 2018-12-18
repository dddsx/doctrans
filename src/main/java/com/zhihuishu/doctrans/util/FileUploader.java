package com.zhihuishu.doctrans.util;

import com.able.base.ftp.oss.OSSPublicUploadInterface;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.io.*;

public class FileUploader {

    public static String uploadFileToOSS(File file) {
        try {
            String responseData = OSSPublicUploadInterface.ftpAttachment(file, "doctrans",
                    "docx2html");
            return getOssUrl(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String uploadFileToOSS(InputStream inputStream, String fileName) {
        try {
            String responseData = OSSPublicUploadInterface.ftpAttachment(inputStream,
                    "doctrans", "docx2html", fileName);
            return getOssUrl(responseData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private static String getOssUrl(String responseData) {
        if (StringUtils.isNotEmpty(responseData)) {
            JSONObject jsonObject = JSONObject.parseObject(responseData);
            JSONObject data = jsonObject.getJSONObject("data");
            if (data != null) {
                return data.getString("path");
            }
        }
        return null;
    }
}
