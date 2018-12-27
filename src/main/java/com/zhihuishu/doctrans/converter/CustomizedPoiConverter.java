package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.converter.support.XWPFDocumentVisitor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;

public class CustomizedPoiConverter extends AbstractDocxConverter {
    
    private XWPFDocument document;
    
    public CustomizedPoiConverter(InputStream inputStream, ConvertSetting setting) throws IOException {
        try (InputStream in = inputStream) {
            this.document = new XWPFDocument(in);
        }
        if (setting != null) {
            this.setting = setting;
        } else {
            this.setting = new ConvertSetting();
        }
    }
    
    @Override
    public String convert() {
        XWPFDocumentVisitor visitor = new XWPFDocumentVisitor(document, setting);
        visitor.visit();
        return visitor.getResult();
    }
}
