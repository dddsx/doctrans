package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.converter.support.XWPFDocumentVisitor;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.InputStream;

public class CustomizedPoiConverter implements DocxConverter {
    
    private final static Logger log = Logger.getLogger(CustomizedPoiConverter.class);
    
    @Override
    public String convert(InputStream docxInput, ConvertSetting settings) {
        XWPFDocument document;
        try {
            document = new XWPFDocument(docxInput);
        } catch (Exception e) {
            log.error("docx文档转换错误", e);
            return "";
        }
        XWPFDocumentVisitor visitor = new XWPFDocumentVisitor(document, settings);
        visitor.visit();
        return visitor.getResult();
    }
}
