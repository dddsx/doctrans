package com.zhihuishu.doctrans.model;

public class MathMLData {
    
    private String id;
    
    private String xmlContent;
    
    public MathMLData(String id, String xmlContent) {
        this.id = id;
        this.xmlContent = xmlContent;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getXmlContent() {
        return xmlContent;
    }
    
    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }
}
