package com.zhihuishu.doctrans.converter.support;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

public class XWPFDocumentVisitor extends AbstractDocumentVisitor {
    
    public XWPFDocumentVisitor(XWPFDocument document, ConvertSetting settings) {
        super(document, settings);
    }
    
    @Override
    protected void startVisitDocument() {
    
    }
    
    @Override
    protected void endVisitDocument() {
    
    }
    
    @Override
    protected void startVisitParagraph() {
    
    }
    
    @Override
    protected void endVisitParagraph() {
    
    }
}
