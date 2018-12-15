package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.converter.support.XWPFDocumentVisitor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.OutputStream;

public class CustomizedPoiConverter implements Converter {
    
    @Override
    public void convert(XWPFDocument document, OutputStream out, ConvertSetting settings) {
        XWPFDocumentVisitor visitor = new XWPFDocumentVisitor(document, settings);
        visitor.visit();
        System.out.println(visitor.getResult());
    }
}
