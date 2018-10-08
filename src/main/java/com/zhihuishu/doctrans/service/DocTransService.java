package com.zhihuishu.doctrans.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface DocTransService {

    /**
     * 将word文档转换成html字符串
     * @param file
     * @param url
     * @param request
     * @return
     */
    String docx2html(MultipartFile file, String url, HttpServletRequest request);
}
