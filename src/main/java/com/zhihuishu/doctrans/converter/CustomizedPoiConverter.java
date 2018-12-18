package com.zhihuishu.doctrans.converter;

import com.zhihuishu.doctrans.converter.support.ConvertSetting;
import com.zhihuishu.doctrans.converter.support.XWPFDocumentVisitor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;

public class CustomizedPoiConverter extends AbstractDocxConverter {
    
    protected final Log logger = LogFactory.getLog(CustomizedPoiConverter.class);
    
    public CustomizedPoiConverter(InputStream inputStream) throws IOException {
        super(inputStream);
    }
    
    public CustomizedPoiConverter(XWPFDocument document) {
        super(document);
    }
    
    @Override
    public String convert(ConvertSetting settings) {
        XWPFDocumentVisitor visitor = new XWPFDocumentVisitor(document, settings);
        visitor.visit();
        return visitor.getResult();
    }
}
