package com.zhihuishu.doctrans.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

public interface DocTransService {

    String docx2html(MultipartFile file, String url, HttpServletRequest request);

}
