package com.zhihuishu.doctrans.converter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * 简单的docx转html接口, 实现方式主要是依靠第三方docx转html类, 并在此基础上自定义wmf矢量图和mathml公式解析
 */
public interface SimpleDocxConverter extends DocxConverter {
    
    void extractWMF(XWPFDocument document);
    
    void extractMathML(XWPFDocument document);
    
}
