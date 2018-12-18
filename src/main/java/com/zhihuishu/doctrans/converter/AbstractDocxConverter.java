package com.zhihuishu.doctrans.converter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractDocxConverter implements DocxConverter {
    
    protected final Log logger = LogFactory.getLog(AbstractDocxConverter.class);
    
    protected XWPFDocument document;
    
    public AbstractDocxConverter(InputStream inputStream) throws IOException {
        this.document = new XWPFDocument(inputStream);
    }
    
    public AbstractDocxConverter(XWPFDocument document) {
        this.document = document;
    }
    
}
