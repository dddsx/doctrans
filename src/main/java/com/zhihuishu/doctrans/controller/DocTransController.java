package com.zhihuishu.doctrans.controller;

import com.zhihuishu.doctrans.service.DocTransService;
import com.zhihuishu.toolkit.log.LoggerTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class DocTransController {

    private final transient LoggerTemplate LOG = LoggerTemplate.getLogger(this.getClass());

    @Resource
    private DocTransService docTransService;

    @ResponseBody
    @RequestMapping("/docx2html")
    public String docx2html(MultipartFile file, String url, HttpServletRequest request) {
        LOG.info("开始转换");
        String html = docTransService.docx2html(file, url, request);
        LOG.info("转换完成");
        return html;
    }
}
