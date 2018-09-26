package com.zhihuishu.doctrans.controller;

import com.zhihuishu.doctrans.service.DocTransService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private DocTransService docTransService;



    @ResponseBody
    @RequestMapping("docx2html")
    public String docx2html (MultipartFile file, String url, HttpServletRequest request){
        String reshtml1 = docTransService.docx2html(file,url,request);
        return reshtml1;
    }

    @ResponseBody
    @RequestMapping("test")
    public String test (){
        return "success";
    }




}
